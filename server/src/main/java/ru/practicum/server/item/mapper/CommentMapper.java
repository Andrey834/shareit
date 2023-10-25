package ru.practicum.server.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.model.Comment;

@Component
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthorId().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}
