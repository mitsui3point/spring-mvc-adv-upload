package hello.upload.domain;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;

    @Builder
    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
