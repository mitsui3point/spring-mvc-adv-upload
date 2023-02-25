package hello.upload.domain;

import hello.upload.dto.ItemForm;
import hello.upload.dto.ItemResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {
    private Long id;
    private String name;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;

    @Builder
    public Item(Long id, String name, UploadFile attachFile, List<UploadFile> imageFiles) {
        this.id = id;
        this.name = name;
        this.attachFile = attachFile;
        this.imageFiles = imageFiles;
    }

    public ItemResult convertItemResult() {
        return ItemResult.builder()
                .id(id)
                .name(name)
                .attachFile(attachFile)
                .imageFiles(imageFiles)
                .build();
    }
}
