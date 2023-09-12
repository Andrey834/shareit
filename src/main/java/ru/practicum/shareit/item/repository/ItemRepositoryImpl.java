package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        final int itemId = item.getId();
        items.put(itemId, item);
        return get(itemId);
    }

    @Override
    public Item update(Item item) {
        final int itemId = item.getId();
        items.put(itemId, item);
        return get(itemId);
    }

    @Override
    public Item get(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
