package ru.practicum.server.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.server.request.model.Request;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequestorIdOrderByCreatedDesc(long userId);

    Page<Request> findRequestsByRequestorIdIsNot(long userId, Pageable pageable);
}
