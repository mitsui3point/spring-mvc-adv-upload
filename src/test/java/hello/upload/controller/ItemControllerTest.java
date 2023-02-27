package hello.upload.controller;

import hello.upload.dto.ItemForm;
import hello.upload.dto.ItemResult;
import hello.upload.repository.ItemRepository;
import hello.upload.restTemplate.multipart.TestRestTemplateMultipartExchanger;
import hello.upload.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.FileInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @GetMapping("/items/new") : 등록 폼을 보여준다.====================================
 * @PostMapping("/items/new") : 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트 한다.====================================
 * @GetMapping("/items/{id}") : 상품을 보여준다.====================================
 * @GetMapping("/images/{filename}") : <img> 태그로 이미지를 조회할 때 사용한다. UrlResource 로 이미지 파일을 읽어서 @ResponseBody 로 이미지 바이너리를 반환한다.
 * @GetMapping("/attach/{itemId}") : 파일을 다운로드 할 때 실행한다.====================================
 * 예제를 더 단순화 할 수 있지만, 파일 다운로드 시 권한 체크같은 복잡한 상황까지 가정한다 생각하고 이미지 id 를 요청하도록 했다.
 * 파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다. 이때는 Content-Disposition 해더에 attachment; filename="업로드 파일명" 값을 주면 된다.
 */
@AutoConfigureMockMvc
public class ItemControllerTest extends TestRestTemplateMultipartExchanger {
    @Value("${file.dir}")
    private String fileDir;
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private static final String RESOURCES_PATH = "src/test/resources/image/";

    @Override
    public void addHeader(HttpHeaders headers) {
        headers.setAccept(List.of(MediaType.TEXT_HTML));
    }

    @BeforeEach
    void setUp() {
        itemRepository.clear();
    }

    @Test
    void newItemTest() throws Exception {
        //given
        String url = "/items/new";

        //when
        ResultActions perform = mvc.perform(get(url));

        //then
        perform.andExpect(view().name("item-form"));
        perform.andExpect(model().attributeExists("itemForm"));
    }

    @Test
    void itemViewTest() throws Exception {
        //given
        String url = "/items/1";

        //when
        ItemResult itemResult = itemService.save(ItemForm.builder().itemName("itemName").build());
        ResultActions perform = mvc.perform(get(url));

        //then
        perform.andExpect(view().name("item-view"));
        perform.andExpect(model().attributeExists("item"));
        perform.andExpect(model().attribute("item", itemResult));
    }

    /**
     * @PostMapping("/items/new"): 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트 한다.
     */
    @Test
    void saveItemTest() throws Exception {
        //given
        String url = "/items/new";
        String itemName = "itemName";

        String attachFileName = "text.txt";
        String imageFileName1 = "testImage.jpg";
        String imageFileName2 = "testImage2.jpg";
        byte[] attachFile = new FileInputStream(RESOURCES_PATH + attachFileName).readAllBytes();
        byte[] imageFile1 = new FileInputStream(RESOURCES_PATH + imageFileName1).readAllBytes();
        byte[] imageFile2 = new FileInputStream(RESOURCES_PATH + imageFileName2).readAllBytes();

        //when
        ResultActions perform = mvc.perform(multipart(url)
                .part(
                        new MockPart("attachFile", attachFileName, attachFile),
                        new MockPart("imageFiles", imageFileName1, imageFile1),
                        new MockPart("imageFiles", imageFileName2, imageFile2))
                .param("itemName", itemName)
        );
        ItemResult actual = itemService.getItemInfo(1L);

        //then
        perform.andExpect(redirectedUrl("/items/1"));
        assertThat(actual).extracting("attachFile").extracting("uploadFileName").isEqualTo(attachFileName);
        assertThat(actual).extracting("imageFiles").asList().element(0).extracting("uploadFileName").isEqualTo(imageFileName1);
        assertThat(actual).extracting("imageFiles").asList().element(1).extracting("uploadFileName").isEqualTo(imageFileName2);
        assertThat(actual).extracting("itemName").isEqualTo("itemName");
    }

    @Test
    void downloadAttachTest() throws Exception {
        //given
        String url = "/attach/";
        String itemName = "itemName";

        String attachFileName = "text.txt";
        byte[] attachFile = new FileInputStream(RESOURCES_PATH + attachFileName).readAllBytes();

        //when
        MockMultipartFile multipartAttachFile = new MockMultipartFile("attachFile", attachFileName, MediaType.TEXT_PLAIN_VALUE, attachFile);
        ItemResult itemResult = itemService.save(
                ItemForm.builder()
                        .itemName(itemName)
                        .attachFile(multipartAttachFile)
                        .build());
        Long id = itemResult.getId();
        String uploadFileName = itemResult.getAttachFile().getUploadFileName();
        ResultActions perform = mvc.perform(get(url + id));

        //then
        perform
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadFileName + "\""))
                .andExpect(header().stringValues(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(attachFile));
    }

    @Test
    void downloadImageTest() throws Exception {
        //given
        String url = "/images/";
        String itemName = "itemName";

        String imageFile1Name = "testImage.jpg";
        byte[] imageFile1 = new FileInputStream(RESOURCES_PATH + imageFile1Name).readAllBytes();
        String imageFile2Name = "testImage2.jpg";
        byte[] imageFile2 = new FileInputStream(RESOURCES_PATH + imageFile2Name).readAllBytes();

        //when
        MockMultipartFile multipartImageFile1 = new MockMultipartFile("imageFile", imageFile1Name, MediaType.IMAGE_JPEG_VALUE, imageFile1);
        MockMultipartFile multipartImageFile2 = new MockMultipartFile("imageFile", imageFile2Name, MediaType.IMAGE_JPEG_VALUE, imageFile2);
        ItemResult savedItemResult = itemService.save(
                ItemForm.builder()
                        .itemName(itemName)
                        .imageFiles(List.of(multipartImageFile1, multipartImageFile2))
                        .build());
        String storeFileName1 = savedItemResult.getImageFiles().get(0).getStoreFileName();
        ResultActions perform = mvc.perform(get(url + storeFileName1));

        //then
        perform.andExpect(content().bytes(imageFile1));
    }

    @AfterEach
    void tearDown() {
        itemRepository.clear();
    }
}
