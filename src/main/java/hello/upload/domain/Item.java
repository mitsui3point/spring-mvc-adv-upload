package hello.upload.domain;

import hello.upload.dto.ItemResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;

    @Builder
    public Item(Long id, String name, UploadFile attachFile, List<UploadFile> imageFiles) {
        this.id = id;
        this.itemName = name;
        this.attachFile = attachFile;
        this.imageFiles = imageFiles;
    }

    public ItemResult convertItemResult() {
        return ItemResult.builder()
                .id(id)
                .itemName(itemName)
                .attachFile(attachFile)
                .imageFiles(imageFiles)
                .build();
    }
}
