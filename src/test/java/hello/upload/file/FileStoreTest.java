package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FileStoreTest {

    @Value("${file.dir}")
    private String fileDir;

    @Autowired
    private FileStore fileStore;

    @BeforeEach
    void setUp() {
        deleteUploadTargetDirContents();
    }

    @AfterEach
    void tearDown() {
        deleteUploadTargetDirContents();
    }

    @Test
    void storeFileTest() throws IOException {
        //given
        String filePath = "src/test/resources/image/";
        String fileName = "text.txt";
        String fileFullPath = filePath + fileName;
        FileInputStream fileInputStream = new FileInputStream(fileFullPath);
        MultipartFile multipartFile = new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, fileInputStream);

        UploadFile expectedUploadFile = UploadFile.builder()
                .uploadFileName(fileName)
                .build();
        byte[] expectedUploadFileBytes = multipartFile.getBytes();

        //when
        UploadFile actualUploadedFile = fileStore.storeFile(multipartFile);
        String storeFileName = actualUploadedFile.getStoreFileName();
        expectedUploadFile.setStoreFileName(storeFileName);

        String storeFileFullPath = fileDir + actualUploadedFile.getStoreFileName();
        byte[] actualUploadFileBytes = new FileInputStream(storeFileFullPath).readAllBytes();

        //then
        assertThat(actualUploadedFile).isEqualTo(expectedUploadFile);
        assertThat(expectedUploadFileBytes).isEqualTo(actualUploadFileBytes);
    }

    @Test
    void storeFilesTest() throws IOException {
        //given
        String filePath = "src/test/resources/image/";
        String[] fileNames = {"testImage.jpg", "testImage2.jpg"};
        String[] fileFullPaths = {filePath + fileNames[0], filePath + fileNames[1]};
        FileInputStream[] fileInputStreams = {
                new FileInputStream(fileFullPaths[0]),
                new FileInputStream(fileFullPaths[1])};

        List<MultipartFile> multipartFiles = Arrays.asList(
                new MockMultipartFile("file", fileNames[0], MediaType.IMAGE_JPEG_VALUE, fileInputStreams[0]),
                new MockMultipartFile("file", fileNames[1], MediaType.IMAGE_JPEG_VALUE, fileInputStreams[1]));

        List<UploadFile> expectedUploadFiles = Arrays.asList(
                UploadFile.builder()
                        .uploadFileName(fileNames[0])
                        .build(),
                UploadFile.builder()
                        .uploadFileName(fileNames[1])
                        .build());
        List<byte[]> expectedUploadFileBytes = Arrays.asList(
                multipartFiles.get(0).getBytes(),
                multipartFiles.get(1).getBytes());

        //when
        List<UploadFile> actualUploadedFiles = fileStore.storeFiles(multipartFiles);
        expectedUploadFiles.get(0).setStoreFileName(actualUploadedFiles.get(0).getStoreFileName());
        expectedUploadFiles.get(1).setStoreFileName(actualUploadedFiles.get(1).getStoreFileName());

        String[] storeFileNames = {
                fileDir + actualUploadedFiles.get(0).getStoreFileName(),
                fileDir + actualUploadedFiles.get(1).getStoreFileName()};
        List<byte[]> actualUploadFileBytes = Arrays.asList(
                new FileInputStream(storeFileNames[0]).readAllBytes(),
                new FileInputStream(storeFileNames[1]).readAllBytes());

        //then
        assertThat(actualUploadedFiles).isEqualTo(expectedUploadFiles);
        assertThat(actualUploadFileBytes).containsAll(expectedUploadFileBytes);
    }

    private void deleteUploadTargetDirContents() {
        File[] files = new File(fileDir).listFiles();
        stream(files).forEach(file -> file.delete());
    }
}
