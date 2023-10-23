package ru.practicum.server.exception.handler;

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
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.user.dto.UserDto;

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
    void handleBadDataItemException() throws Exception {
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

        CommentDto commentDto = new CommentDto();
        commentDto.setText("now");
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleBookingNotFoundException() throws Exception {
        mvc.perform(patch("/bookings/{wrongId}", 999)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}