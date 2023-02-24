package hello.upload.domain;

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
}
