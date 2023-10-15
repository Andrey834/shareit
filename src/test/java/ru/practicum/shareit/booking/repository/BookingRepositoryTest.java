package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Booking booking1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@ya.ru");

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@ya.ru");

        item1 = new Item();
        item1.setName("item1");
        item1.setDescription("disco item1");
        item1.setOwner(user2);
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("disco item2");
        item2.setOwner(user1);
        item2.setAvailable(true);

        booking1 = new Booking();
        booking1.setBooker(user1);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(LocalDateTime.now().plusSeconds(100));
        booking1.setEnd(LocalDateTime.now().plusSeconds(300));
        booking1.setItem(item1);

        Booking booking2 = new Booking();
        booking2.setBooker(user2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStart(LocalDateTime.now().plusSeconds(100));
        booking2.setEnd(LocalDateTime.now().plusSeconds(300));
        booking2.setItem(item2);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
    }

    @Test
    void findAllByBookerId() {
        int bookerId = user1.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        Page<Booking> pages = bookingRepository.findAllByBookerId(bookerId, PageRequest.of(0, 10));

        expectedSize = 1;
        actualSize = pages.getContent().size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(pages);
    }

    @Test
    void findAllByItemOwnerId() {
        int ownerId = user2.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        Page<Booking> pages = bookingRepository.findAllByItemOwnerId(ownerId, PageRequest.of(0, 10));

        expectedSize = 1;
        actualSize = pages.getContent().size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(pages);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfter() {
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        bookingRepository.save(booking1);

        int bookerId = user1.getId();
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println(bookings);
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfter(
                        bookerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(0, 10)
                );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfter() {
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        bookingRepository.save(booking1);

        int ownerId = user2.getId();
        List<Booking> bookings = bookingRepository.findAll();
        System.out.println(bookings);
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                        ownerId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(0, 10)
                );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByBookerIdAndStartAfter() {
        int bookerId = user1.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStartAfter(
                bookerId,
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByItemOwnerIdAndStartAfter() {
        int ownerId = user2.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                ownerId,
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByBookerIdAndEndBefore() {
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        booking1.setEnd(LocalDateTime.now().minusSeconds(10));
        bookingRepository.save(booking1);

        int bookerId = user1.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndBefore(
                bookerId,
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByItemOwnerIdAndEndBefore() {
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        booking1.setEnd(LocalDateTime.now().minusSeconds(10));
        bookingRepository.save(booking1);

        int ownerId = user2.getId();
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(
                ownerId,
                LocalDateTime.now(),
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        int ownerId = user2.getId();
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(
                ownerId,
                status,
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByBookerIdAndStatus() {
        booking1.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);

        int bookerId = user1.getId();
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndStatus(
                bookerId,
                status,
                PageRequest.of(0, 10)
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findAllByBookerIdAndEndBeforeAndStatus() {
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        booking1.setEnd(LocalDateTime.now().minusSeconds(10));
        bookingRepository.save(booking1);

        int bookerId = user1.getId();
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(
                bookerId,
                LocalDateTime.now(),
                status
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }

    @Test
    void findBookingByItemIdAndItemOwnerIdAndStatus() {
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(LocalDateTime.now().minusSeconds(100));
        booking1.setEnd(LocalDateTime.now().minusSeconds(10));
        bookingRepository.save(booking1);

        int ownerId = user2.getId();
        int itemId = item1.getId();
        BookingStatus status = BookingStatus.APPROVED;
        List<Booking> bookings = bookingRepository.findAll();
        int expectedSize = 2;
        int actualSize = bookings.size();
        assertEquals(expectedSize, actualSize);

        List<Booking> bookingList = bookingRepository.findBookingByItemIdAndItemOwnerIdAndStatus(
                itemId,
                ownerId,
                status
        );

        expectedSize = 1;
        actualSize = bookingList.size();
        assertEquals(expectedSize, actualSize);
        assertThat(booking1).isIn(bookingList);
    }
}