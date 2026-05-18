package ru.practicum.server.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    @Test
    void toUserDto_mapsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    void toUser_mapsAllFields() {
        UserDto dto = new UserDto();
        dto.setId(2L);
        dto.setName("Bob");
        dto.setEmail("bob@example.com");

        User user = UserMapper.toUser(dto);

        assertEquals(2L, user.getId());
        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
    }

    @Test
    void listToUserDto_mapsAllElements() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@a.com");

        User u2 = new User();
        u2.setId(2L);
        u2.setName("B");
        u2.setEmail("b@b.com");

        List<UserDto> result = UserMapper.listToUserDto(List.of(u1, u2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
    }

    @Test
    void toUserDto_thenToUser_isSymmetric() {
        User original = new User();
        original.setId(5L);
        original.setName("Charlie");
        original.setEmail("charlie@example.com");

        User roundTripped = UserMapper.toUser(UserMapper.toUserDto(original));

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getName(), roundTripped.getName());
        assertEquals(original.getEmail(), roundTripped.getEmail());
    }
}
