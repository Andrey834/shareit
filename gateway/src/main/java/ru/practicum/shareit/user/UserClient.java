package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    @CachePut(cacheNames = "users")
    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    @CachePut(cacheNames = "users")
    public ResponseEntity<Object> update(@PathVariable long id, @RequestBody @Valid UserDto userDto) {
        return patch("/" + id, userDto);
    }

    @Cacheable(cacheNames = "users", key = "#id")
    public ResponseEntity<Object> get(@PathVariable long id) {
        return get("/" + id);
    }

    @Cacheable(cacheNames = "users")
    public ResponseEntity<Object> getAll() {
        return get("");
    }

    @CacheEvict(cacheNames = "users", key = "#id")
    public void delete(@PathVariable long id) {
        delete("/" + id);
    }
}
