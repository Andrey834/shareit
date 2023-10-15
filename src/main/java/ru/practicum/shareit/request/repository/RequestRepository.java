package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequestorIdOrderByCreatedDesc(Integer userId);

    Page<Request> findRequestsByRequestorIdIsNot(Integer userId, Pageable pageable);
}
