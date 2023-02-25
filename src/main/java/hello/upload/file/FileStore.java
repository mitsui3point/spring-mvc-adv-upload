package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public UploadFile storeFile(MultipartFile file) throws IOException {
        if (file == null) return null;

        String uploadFileName = file.getOriginalFilename();
        String extension = extractExtension(uploadFileName);
        String storeFileName = UUID.randomUUID().toString() + "." + extension;

        file.transferTo(new File(getUploadFullFilePath(storeFileName)));

        return UploadFile.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .build();
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> result = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            result.add(storeFile(multipartFile));
        }
        return result;
    }

    private String getUploadFullFilePath(String storeFileName) {
        return fileDir + storeFileName;
    }

    private String extractExtension(String fileName) {
        return fileName.substring(
                fileName.lastIndexOf(".") + 1);
    }
}
