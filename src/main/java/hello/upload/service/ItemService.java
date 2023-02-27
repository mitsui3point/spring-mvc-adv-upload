package hello.upload.service;

import hello.upload.domain.Item;
import hello.upload.domain.UploadFile;
import hello.upload.dto.ItemForm;
import hello.upload.dto.ItemResult;
import hello.upload.file.FileStore;
import hello.upload.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    public ItemResult save(ItemForm itemForm) throws IOException {

        Item item = Item.builder()
                .name(itemForm.getItemName())
                .attachFile(storeAndGetAttachFile(itemForm))
                .imageFiles(storeAndGetImageFiles(itemForm))
                .build();

        return itemRepository.save(item)
                .convertItemResult();
    }

    public ItemResult getItemInfo(Long id) {
        return itemRepository.findById(id)
                .orElseGet(() -> Item.builder().build())
                .convertItemResult();
    }


    private List<UploadFile> storeAndGetImageFiles(ItemForm itemForm) throws IOException {
        List<UploadFile> imageFiles = null;
        if (itemForm.getImageFiles() != null && itemForm.getImageFiles().size() > 0) {
            imageFiles = fileStore.storeFiles(itemForm.getImageFiles());
        }
        return imageFiles;
    }

    private UploadFile storeAndGetAttachFile(ItemForm itemForm) throws IOException {
        UploadFile attachFile = null;
        if (itemForm.getAttachFile() != null) {
            attachFile = fileStore.storeFile(itemForm.getAttachFile());
        }
        return attachFile;
    }

    public Resource downloadFile(String storeFileName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getUploadFilePath(storeFileName));
    }
}
