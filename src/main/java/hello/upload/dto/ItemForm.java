package hello.upload.dto;

import hello.upload.domain.Item;
import hello.upload.domain.UploadFile;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemForm {
    private Long itemId;
    private String itemName;
    private MultipartFile attachFile;
    private List<MultipartFile> imageFiles;

    @Builder
    public ItemForm(Long itemId, String itemName, MultipartFile attachFile, List<MultipartFile> imageFiles) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.attachFile = attachFile;
        this.imageFiles = imageFiles;
    }

//    public Item convertItem() {
//        UploadFile convertedAttachFile = null;
//        if (attachFile != null) {
//            convertedAttachFile = getUploadFile(attachFile);
//        }
//        List<UploadFile> convertedImageFiles = new ArrayList<>();
//        if (imageFiles != null) {
//            imageFiles.stream()
//                    .forEach(o -> {
//                        if (o != null) convertedImageFiles.add(getUploadFile(o));
//                    });
//        }
//        return Item.builder()
//                .id(itemId)
//                .name(itemName)
//                .attachFile(convertedAttachFile)
//                .imageFiles(convertedImageFiles)
//                .build();
//    }

    private UploadFile getUploadFile(MultipartFile file) {
        return UploadFile.builder()
                .uploadFileName(file.getOriginalFilename())
                .build();
    }
}
