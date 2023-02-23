package hello.upload.restTemplate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestRestTemplateExchanger {

    public static final String LOCALHOST_URL = "http://localhost:%d%s";

    public abstract void addHeader(HttpHeaders headers);

    @LocalServerPort
    private Integer port;

    public ResponseEntity<String> getResponseEntity(String url, HttpMethod httpMethod) {
        return new TestRestTemplate().exchange(
                LOCALHOST_URL.formatted(port, url),
                httpMethod,
                new HttpEntity<>("body", getHttpHeaders()),
                String.class
        );
    }


    protected HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        addHeader(headers);
        return headers;
    }

    public Integer getPort() {
        return port;
    }
}