package hello.upload.controller;

import hello.upload.domain.UploadFile;
import hello.upload.dto.ItemForm;
import hello.upload.dto.ItemResult;
import hello.upload.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    @Value("${file.dir}")
    private String fileDir;

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm itemForm) {
        return "item-form";
    }

    @GetMapping("/items/{id}")//@GetMapping("/items/{id}") : 상품을 보여준다.
    public String items(@PathVariable Long id,
                        Model model) {
        ItemResult itemInfo = itemService.getItemInfo(id);
        model.addAttribute("item", itemInfo);
        return "item-view";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm itemForm) throws IOException {
        ItemResult itemResult = itemService.save(itemForm);
        return "redirect:/items/" + itemResult.getId();
    }

    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable("itemId") Long itemId) throws MalformedURLException {
        UploadFile attachFile = itemService.getItemInfo(itemId)
                .getAttachFile();
        String storeFileName = attachFile.getStoreFileName();
        String uploadFileName = attachFile.getUploadFileName();
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        Resource resource = itemService.downloadFile(storeFileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedUploadFileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(resource);
    }

    @GetMapping("/images/{filename}")
    @ResponseBody
    public Resource downloadImageFile(@PathVariable("filename") String fileName) throws MalformedURLException {
        return itemService.downloadFile(fileName);
    }
}
