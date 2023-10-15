package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByItemId_Id() {
        Item item = itemRepository.save(new Item());
        Comment comment = new Comment();
        comment.setItemId(item);
        comment.setText("new comment");
        commentRepository.save(comment);

        List<Comment> expectedCommentList = commentRepository.findAllByItemId_Id(item.getId());

        int expectedSize = 1;
        int actualSize = expectedCommentList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(comment).isIn(expectedCommentList);
    }
}