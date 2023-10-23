package ru.practicum.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServerApplicationTest {

    @Test
    void contextLoads() {
        ServerApplication.main(new String[]{});
    }
}