package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private Item item1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@ya.ru");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@ya.ru");

        item1 = new Item();
        item1.setName("item1");
        item1.setDescription("disco item1");
        item1.setOwner(user1);
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("disco item2");
        item2.setOwner(user2);
        item2.setAvailable(true);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void findByOwnerIdAndIdIs() {
        List<Item> items = itemRepository.findAll();
        int expectedSize = 2;
        int actualSize = items.size();

        assertEquals(expectedSize, actualSize);

        Optional<Item> actualItem = itemRepository.findByOwnerIdAndIdIs(user1.getId(), item1.getId());

        assertTrue(actualItem.isPresent());
        assertEquals(item1, actualItem.get());
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAll();
        int expectedSize = 2;
        int actualSize = items.size();
        assertEquals(expectedSize, actualSize);

        Page<Item> pages = itemRepository.findAllByOwnerId(user1.getId(), PageRequest.of(0, 10));
        expectedSize = 1;
        actualSize = pages.getContent().size();
        assertEquals(expectedSize, actualSize);

        assertThat(item1).isIn(pages);
    }

    @Test
    void findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        String search = item1.getName().toUpperCase();
        List<Item> items = itemRepository.findAll();
        int expectedSize = 2;
        int actualSize = items.size();
        assertEquals(expectedSize, actualSize);

        Page<Item> pages = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        search,
                        search,
                        PageRequest.of(0, 10)
                );

        expectedSize = 1;
        actualSize = pages.getContent().size();
        assertEquals(expectedSize, actualSize);

        assertThat(item1).isIn(pages);
    }
}