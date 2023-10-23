package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @Caching(evict = {
            @CacheEvict(value = "itemsList", allEntries = true),
            @CacheEvict(value = "items", allEntries = true),
            @CacheEvict(value = "allRequests", allEntries = true),
            @CacheEvict(value = "ownerRequest", allEntries = true),
            @CacheEvict(value = "request", allEntries = true)})
    public ResponseEntity<Object> create(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    @Caching(evict = {
            @CacheEvict(value = "itemsList", allEntries = true),
            @CacheEvict(value = "items", allEntries = true),
            @CacheEvict(value = "allRequests", allEntries = true),
            @CacheEvict(value = "ownerRequest", allEntries = true),
            @CacheEvict(value = "request", allEntries = true)})
    public ResponseEntity<Object> update(long userId, ItemDto itemDto, long itemId) {
        return patch("/" + itemId, userId, itemDto);
    }

    @Cacheable(cacheNames = "items", key = "#userId + '_' + #itemId")
    public ResponseEntity<Object> get(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    @Caching(evict = {
            @CacheEvict(value = "itemsList", allEntries = true),
            @CacheEvict(value = "items", allEntries = true)}
    )
    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    @Cacheable(cacheNames = "itemsList", key = "#userId + '_' + #from + '_' + #size")
    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    @Cacheable(cacheNames = "itemsList", key = "#userId + '_' + #text + '_' + #from + '_' + #size")
    public ResponseEntity<Object> search(long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}

