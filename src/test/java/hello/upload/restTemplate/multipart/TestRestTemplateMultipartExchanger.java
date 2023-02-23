package hello.upload.restTemplate.multipart;

import hello.upload.restTemplate.TestRestTemplateExchanger;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;

//@ContextConfiguration
public abstract class TestRestTemplateMultipartExchanger extends TestRestTemplateExchanger {

    public ResponseEntity<String> getMultipartSingleFileResponseEntity(String url, String fieldName, String filePath) throws IOException {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(fieldName, new InputStreamResource(new FileInputStream(filePath)));

        // when
        return new TestRestTemplate().postForEntity(
                LOCALHOST_URL.formatted(super.getPort(), url),
                new HttpEntity<>(body, super.getHttpHeaders()),
                String.class);
    }

}
