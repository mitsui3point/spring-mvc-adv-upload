package hello.upload.controller;

import hello.upload.restTemplate.multipart.TestRestTemplateMultipartExchanger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class ServletUploadControllerV1FailTest extends TestRestTemplateMultipartExchanger {

    private static final String RESOURCES_IMAGE_PATH = "src/test/resources/image/";

    @Test
    void saveFileV1ExceedFileSizeTest() throws Exception {
        //given
        String url = "/servlet/v1/upload";

        String fileName = "highQualityImage";
        String contentType = "jpg";
        String filePath = "%s%s.%s".formatted(RESOURCES_IMAGE_PATH, fileName, contentType);
        String fileFieldName = "file";

        //when
        HttpStatusCode statusCode = getMultipartSingleFileResponseEntity(url,
                fileFieldName,
                filePath)
                .getStatusCode();

        assertThat(statusCode).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Override
    public void addHeader(HttpHeaders headers) {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    }
}
