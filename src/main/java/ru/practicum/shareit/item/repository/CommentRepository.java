package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId_Id(Integer itemId);
}
