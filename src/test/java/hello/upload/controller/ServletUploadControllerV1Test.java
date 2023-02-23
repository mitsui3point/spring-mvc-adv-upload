package hello.upload.controller;

import hello.upload.restTemplate.multipart.TestRestTemplateMultipartExchanger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_HTML;

public class ServletUploadControllerV1Test extends TestRestTemplateMultipartExchanger {

    private static final String RESOURCES_IMAGE_PATH = "src/test/resources/image/";

    @Test
    void newFileTest() {
        String url = "/servlet/v1/upload";

        ResponseEntity<String> responseEntity = getResponseEntity(url, GET);
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        assertThat(statusCode).isEqualTo(OK);
    }

    @Test
    void saveFileV1Test() throws Exception {
        //given
        String url = "/servlet/v1/upload";

        String fileName = "testImage";
        String contentType = "jpg";
        String filePath = "%s%s.%s".formatted(RESOURCES_IMAGE_PATH, fileName, contentType);
        String fileFieldName = "file";

        //when
        ResponseEntity<String> responseEntity = getMultipartSingleFileResponseEntity(url, fileFieldName, filePath);
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        assertThat(statusCode).isEqualTo(OK);
    }

    @Override
    public void addHeader(HttpHeaders headers) {
        if (isCallerMethod("saveFileV1Test")) {
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
}
