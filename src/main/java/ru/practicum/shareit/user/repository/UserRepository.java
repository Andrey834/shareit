package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.PersistenceContext;

@Repository
@EnableJpaRepositories
@PersistenceContext
public interface UserRepository extends JpaRepository<User, Integer> {
}
