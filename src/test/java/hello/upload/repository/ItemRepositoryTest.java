package hello.upload.repository;


import hello.upload.domain.Item;
import hello.upload.domain.UploadFile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ItemRepositoryTest {
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepository();
    }

    @Test
    void saveTest() {
        Item item = getTestItem(1);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        assertThat(savedItem).extracting("name").isEqualTo(item.getName());
        assertThat(savedItem).extracting("attachFile").isEqualTo(item.getAttachFile());
        assertThat(savedItem).extracting("imageFiles").isEqualTo(item.getImageFiles());
    }

    @Test
    void findByIdTest() {
        //given
        List<Item> saveItems = new ArrayList<>();
        int index = 0;
        while(index < 5) {
            Item savedItem = itemRepository.save(getTestItem(++index));
            saveItems.add(savedItem);
        }
        Item expectedItem = saveItems.get(2);

        //when
        Optional<Item> actualItem = itemRepository.findById(3L);

        //then
        assertThat(actualItem).isPresent();
        assertThat(actualItem.get()).extracting("name").isEqualTo(expectedItem.getName());
        assertThat(actualItem.get()).extracting("attachFile").isEqualTo(expectedItem.getAttachFile());
        assertThat(actualItem.get()).extracting("imageFiles").isEqualTo(expectedItem.getImageFiles());
    }

    private Item getTestItem(int index) {
        UploadFile attachFile = UploadFile.builder()
                .uploadFileName("attached_%d.txt".formatted(index))
                .storeFileName(UUID.randomUUID().toString())
                .build();
        UploadFile image1 = UploadFile.builder()
                .uploadFileName("image1_%d.png".formatted(index))
                .storeFileName(UUID.randomUUID().toString())
                .build();
        UploadFile image2 = UploadFile.builder()
                .uploadFileName("image2_%d.png".formatted(index))
                .build();
        List<UploadFile> imageFiles = Arrays.asList(image1, image2);

        return Item.builder()
                .name("itemName_%d".formatted(index))
                .attachFile(attachFile)
                .imageFiles(imageFiles)
                .build();
    }
}
