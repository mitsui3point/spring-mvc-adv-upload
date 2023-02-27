package hello.upload.dto;

import hello.upload.domain.UploadFile;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResult {
    private Long id;
    private String itemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;

    @Builder
    public ItemResult(Long id, String itemName, UploadFile attachFile, List<UploadFile> imageFiles) {
        this.id = id;
        this.itemName = itemName;
        this.attachFile = attachFile;
        this.imageFiles = imageFiles;
    }

}
