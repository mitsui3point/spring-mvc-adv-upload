package hello.upload.controller;

import hello.upload.restTemplate.multipart.TestRestTemplateMultipartExchanger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_HTML;

public class SpringUploadControllerTest extends TestRestTemplateMultipartExchanger {

    private static final String RESOURCES_IMAGE_PATH = "src/test/resources/image/";

    @Value("${file.dir}")
    private String uploadTargetDir;

    @BeforeEach
    void setUp() throws InterruptedException {
        deleteUploadTargetDirContents();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        deleteUploadTargetDirContents();
    }

    @Test
    void newFileTest() {
        String url = "/spring/upload";

        ResponseEntity<String> responseEntity = getResponseEntity(url, GET);
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        assertThat(statusCode).isEqualTo(OK);
    }

    @Test
    void saveFileTest() throws Exception {
        //given
        String url = "/spring/upload";

        String fileName = "text.txt";
        String filePath = RESOURCES_IMAGE_PATH + fileName;
        String fileFieldName = "file";

        String textFieldName = "text";
        String textValue = "Spring";

        byte[] expectedBytes = new FileInputStream(filePath).readAllBytes();

        //when
        ResponseEntity<String> responseEntity = getMultipartSingleFileResponseEntity(
                url,
                fileFieldName,
                filePath,
                fileName,
                textFieldName,
                textValue);
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        byte[] actualBytes = new FileInputStream(uploadTargetDir + fileName).readAllBytes();

        //then
        assertThat(statusCode).isEqualTo(OK);
        assertThat(actualBytes).isEqualTo(expectedBytes);
    }

    @Override
    public void addHeader(HttpHeaders headers) {
        if (isCallerMethod("saveFileTest")) {
            headers.setContentType(MULTIPART_FORM_DATA);
            return;
        }
        headers.setContentType(TEXT_HTML);
    }

    private boolean isCallerMethod(String methodName) {
        return stream(currentThread().getStackTrace())
                .filter(o -> o
                        .getMethodName()
                        .equals(methodName))
                .findFirst()
                .isPresent();
    }

    private void deleteUploadTargetDirContents() throws InterruptedException {
        stream(new File(uploadTargetDir).listFiles())
                .forEach(o -> o.delete());
        Thread.sleep(500);
    }
}
