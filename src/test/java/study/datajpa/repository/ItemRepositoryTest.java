package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {
    @Autowired ItemRepository itemRepository;

    @Test
    @DisplayName("이건 persist 일까 머지일까?")
    void save() throws Exception {
        Item item = new Item("A");
        itemRepository.save(item);
    }
}