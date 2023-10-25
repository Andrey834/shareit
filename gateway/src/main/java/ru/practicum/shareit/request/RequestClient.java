package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
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
    public ResponseEntity<Object> create(long userId, RequestDto requestDto) {
        return post("", userId, requestDto);
    }

    @Cacheable(cacheNames = "request", key = "#userId + '_' + #requestId")
    public ResponseEntity<Object> get(long userId, Integer requestId) {
        return get("/" + requestId, userId);
    }

    @Cacheable(cacheNames = "ownerRequest", key = "#userId")
    public ResponseEntity<Object> getOwnerRequest(long userId) {
        return get("", userId);
    }

    @Cacheable(cacheNames = "allRequests", key = "#userId + '_' + #from + '_' + #size")
    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
