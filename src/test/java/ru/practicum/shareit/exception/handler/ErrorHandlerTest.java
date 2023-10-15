package ru.practicum.shareit.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ErrorHandlerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleUserNotFoundException() throws Exception {
        int wrongId = 999;
        mvc.perform(get("/users/{userId}", wrongId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleUserWithoutEmailException() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleBadDataRequestException() throws Exception {
        RequestDto requestDto = new RequestDto();
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleRequestNotFoundException() throws Exception {
        var user = new UserDto();
        user.setName("john");
        user.setEmail("john@ya.ru");
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("desc");
        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleBadDataItemException() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleNotFoundItemException() throws Exception {
        var user = new UserDto();
        user.setName("john");
        user.setEmail("john@ya.ru");
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        mvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleWrongOwnerItemException() throws Exception {
        mvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleNotAvailableItemException() throws Exception {
        var user = new UserDto();
        user.setName("john");
        user.setEmail("john@ya.ru");
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));


        ItemDto item = new ItemDto();
        item.setAvailable(false);
        item.setName("item1");
        item.setDescription("descr");
        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));


        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(100));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(200));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleBadDataBookingException() throws Exception {
        var user = new UserDto();
        user.setName("john");
        user.setEmail("john@ya.ru");
        var user2 = new UserDto();
        user2.setName("john2");
        user2.setEmail("john2@ya.ru");
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));


        ItemDto item = new ItemDto();
        item.setAvailable(true);
        item.setName("item1");
        item.setDescription("descr");
        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)));


        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1);
        bookingDto.setStart(LocalDateTime.now().plusSeconds(100));
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleUnsupportedStatusBookingException() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WRONG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleBookingNotFoundException() throws Exception {
        mvc.perform(patch("/bookings/{wrongId}", 999)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void handleNoAccessBookingException() {
    }

    @Test
    void handleWrongUserApproveException() {
    }
}