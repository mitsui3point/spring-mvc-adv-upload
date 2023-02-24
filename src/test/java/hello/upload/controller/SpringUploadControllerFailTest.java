
package hello.upload.controller;

import hello.upload.restTemplate.multipart.TestRestTemplateMultipartExchanger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class SpringUploadControllerFailTest extends TestRestTemplateMultipartExchanger {

    private static final String RESOURCES_IMAGE_PATH = "src/test/resources/image/";

    @Test
    void saveFileExceedFileSizeTest() throws Exception {
        //given
        String url = "/spring/upload";

        String fileName = "highQualityImage.jpg";
        String filePath = RESOURCES_IMAGE_PATH + fileName;
        String fileFieldName = "file";

        String textFieldName = "text";
        String textValue = "Spring";

        //when
        HttpStatusCode statusCode = getMultipartSingleFileResponseEntity(url,
                fileFieldName,
                filePath,
                fileName,
                textFieldName,
                textValue)
                .getStatusCode();

        assertThat(statusCode).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Override
    public void addHeader(HttpHeaders headers) {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    }

}
