package ru.practicum.server.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void toCommentDto_mapsAllFields() {
        User author = new User();
        author.setId(10L);
        author.setName("Alice");
        author.setEmail("alice@example.com");

        Item item = new Item();
        item.setId(5L);
        item.setName("Drill");

        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals("Great item!", dto.getText());
        assertEquals("Alice", dto.getAuthorName());
        assertEquals(now, dto.getCreated());
    }
}
