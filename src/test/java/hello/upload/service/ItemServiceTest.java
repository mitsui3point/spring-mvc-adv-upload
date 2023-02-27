package hello.upload.service;

import hello.upload.domain.UploadFile;
import hello.upload.dto.ItemForm;
import hello.upload.dto.ItemResult;
import hello.upload.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@SpringBootTest
public class ItemServiceTest {
    @Value("${file.dir}")
    private String fileDir;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() throws InterruptedException {
        deleteUploadTargetDirContents();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        deleteUploadTargetDirContents();
        itemRepository.clear();
    }

    @Test
    void saveTest() throws IOException {
        //given
        String filePath = "src/test/resources/image/";

        MultipartFile testAttachFile = getTestAttachFile(filePath);
        List<MultipartFile> testImageFiles = getTestImageFiles(filePath);

        ItemForm itemForm = ItemForm.builder()
                .itemName("itemName")
                .attachFile(testAttachFile)
                .imageFiles(testImageFiles)
                .build();

        //when
        ItemResult actual = itemService.save(itemForm);

        //then
        itemResultAssert(testAttachFile, testImageFiles, actual, itemForm.getItemName());
    }

    @Test
    void getItemInfoTest() throws IOException {
        //given
        String filePath = "src/test/resources/image/";

        MultipartFile testAttachFile = getTestAttachFile(filePath);
        List<MultipartFile> testImageFiles = getTestImageFiles(filePath);

        ItemForm itemForm1 = ItemForm.builder()
                .itemName("itemName1")
                .attachFile(testAttachFile)
                .imageFiles(testImageFiles)
                .build();

        ItemForm itemForm2 = ItemForm.builder()
                .itemName("itemName2")
                .attachFile(testAttachFile)
                .imageFiles(testImageFiles)
                .build();

        //when
        ItemResult[] saveItemResults = {itemService.save(itemForm1), itemService.save(itemForm2)};
        ItemResult actual = itemService.getItemInfo(1L);

        //then
        assertThat(saveItemResults[0]).isEqualTo(actual);
        itemResultAssert(testAttachFile, testImageFiles, actual, itemForm1.getItemName());
    }

    @Test
    void downloadFileTest() throws IOException {
        //given
        String filePath = "src/test/resources/image/";
        MultipartFile testAttachFile = getTestAttachFile(filePath);
        List<MultipartFile> testImageFiles = getTestImageFiles(filePath);

        ItemForm itemForm = ItemForm.builder()
                .itemName("itemName1")
                .attachFile(testAttachFile)
                .imageFiles(testImageFiles)
                .build();
        String storeFileName = itemService.save(itemForm).getAttachFile().getStoreFileName();

        //when
        Resource actual = itemService.downloadFile(storeFileName);

        //then
        Assertions.assertThat(actual).isEqualTo(new UrlResource("file:" + fileDir + storeFileName));
    }

    private void itemResultAssert(MultipartFile testAttachFile, List<MultipartFile> testImageFiles, ItemResult actual, String expectedItemName) throws IOException {
        //item info
        String attachFileName = actual.getAttachFile()
                .getUploadFileName();
        String imageFirstFileName = actual.getImageFiles()
                .get(0)
                .getUploadFileName();
        String imageSecondFileName = actual.getImageFiles()
                .get(1)
                .getUploadFileName();
        String storeAttachFileName = actual.getAttachFile()
                .getStoreFileName();
        assertThat(actual).extracting("id").isEqualTo(1L);
        assertThat(actual).extracting("itemName").isEqualTo(expectedItemName);
        assertThat(attachFileName).isEqualTo("text.txt");
        assertThat(imageFirstFileName).isEqualTo("testImage.jpg");
        assertThat(imageSecondFileName).isEqualTo("testImage2.jpg");

        //item attach file
        byte[] actualAttachFileBytes = new FileInputStream(fileDir + storeAttachFileName).readAllBytes();
        byte[] expectedAttachFileBytes = testAttachFile.getBytes();
        assertThat(actualAttachFileBytes).isEqualTo(expectedAttachFileBytes);

        //item image files
        int index = 0;
        String storeImageFileName;
        byte[] actualImageFileBytes;
        byte[] expectedImageFileBytes;
        for (UploadFile imageFile : actual.getImageFiles()) {
            storeImageFileName = imageFile.getStoreFileName();
            actualImageFileBytes = new FileInputStream(fileDir + storeImageFileName).readAllBytes();
            expectedImageFileBytes = testImageFiles.get(index).getBytes();
            assertThat(actualImageFileBytes).isEqualTo(expectedImageFileBytes);
            index++;
        }
    }

    private List<MultipartFile> getTestImageFiles(String filePath) throws IOException {
        String[] fileNames = {"testImage.jpg", "testImage2.jpg"};
        String[] fileFullPaths = {filePath + fileNames[0], filePath + fileNames[1]};
        String fieldName = "files";
        return Arrays.asList(
                new MockMultipartFile(
                        fieldName,
                        fileNames[0],
                        IMAGE_JPEG_VALUE,
                        new FileInputStream(fileFullPaths[0])),
                new MockMultipartFile(
                        fieldName,
                        fileNames[1],
                        IMAGE_JPEG_VALUE,
                        new FileInputStream(fileFullPaths[1]))
        );
    }

    private MultipartFile getTestAttachFile(String filePath) throws IOException {
        String fileName = "text.txt";
        String fileFullPath = filePath + fileName;
        String fieldName = "file";
        return new MockMultipartFile(
                fieldName,
                fileName,
                IMAGE_JPEG_VALUE,
                new FileInputStream(fileFullPath));
    }

    private void deleteUploadTargetDirContents() throws InterruptedException {
        File[] files = new File(fileDir).listFiles();
        stream(files).forEach(file -> file.delete());
        Thread.sleep(500);
    }
}
