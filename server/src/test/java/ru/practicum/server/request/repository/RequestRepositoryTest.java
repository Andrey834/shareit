package ru.practicum.server.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class RequestRepositoryTest {
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private Request request1;
    private Request request2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@ya.ru");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@ya.ru");

        request1 = new Request();
        request1.setRequestor(user1);
        request1.setDescription("descr");
        request1.setCreated(LocalDateTime.now());

        request2 = new Request();
        request2.setRequestor(user2);
        request2.setDescription("descr");
        request2.setCreated(LocalDateTime.now());

        userRepository.save(user1);
        userRepository.save(user2);
        requestRepository.save(request1);
        requestRepository.save(request2);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<Request> reqList = requestRepository.findAll();
        int expectedSize = 2;
        int actualSize = reqList.size();

        assertThat(actualSize).isEqualTo(expectedSize);

        List<Request> expectedReqList = requestRepository
                .findAllByRequestorIdOrderByCreatedDesc(user1.getId());
        expectedSize = 1;
        actualSize = expectedReqList.size();

        assertThat(actualSize).isEqualTo(expectedSize);
        assertThat(request1).isIn(expectedReqList);
        assertThat(request2).isNotIn(expectedReqList);
    }

    @Test
    void findRequestsByRequestorIdIsNot() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Request> reqList = requestRepository.findAll();
        int expectedSize = 2;
        int actualSize = reqList.size();

        assertThat(actualSize).isEqualTo(expectedSize);

        Page<Request> expectedReqPage = requestRepository
                .findRequestsByRequestorIdIsNot(user1.getId(), pageRequest);
        expectedSize = 1;
        actualSize = expectedReqPage.getContent().size();

        assertThat(actualSize).isEqualTo(expectedSize);
        assertThat(request1).isNotIn(expectedReqPage.getContent());
        assertThat(request2).isIn(expectedReqPage.getContent());
    }
}