package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByOwnerIdAndIdIs(Integer owner, Integer id);

    List<Item> findAllByOwnerId(Integer id);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
            String strName,
            String strDesc
    );
}
