package hello.upload.restTemplate.multipart;

import hello.upload.restTemplate.TestRestTemplateExchanger;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;

public abstract class TestRestTemplateMultipartExchanger extends TestRestTemplateExchanger {

    public ResponseEntity<String> getMultipartSingleFileResponseEntity(String url,
                                                                       String fileFieldName,
                                                                       String filePath,
                                                                       String fileName,
                                                                       String textFieldName,
                                                                       String textValue) throws IOException {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(textFieldName, textValue);
        body.add(fileFieldName, getFilePartHttpEntity(fileFieldName, filePath, fileName));

        // when
        return new TestRestTemplate().exchange(LOCALHOST_URL.formatted(super.getPort(), url),
                HttpMethod.POST,
                new HttpEntity<>(body, super.getHttpHeaders()),
                String.class);
    }

    private HttpEntity<byte[]> getFilePartHttpEntity(String fileFieldName, String filePath, String fileName) throws IOException {
        InputStreamResource resource = new InputStreamResource(new FileInputStream(filePath));

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentDispositionFormData(fileFieldName, fileName);

        return new HttpEntity<>(FileCopyUtils.copyToByteArray(
                resource.getInputStream()),
                fileHeaders);
    }
}
