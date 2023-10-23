package ru.practicum.server.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingState;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.booking.exception.BookingNotFoundException;
import ru.practicum.server.booking.exception.NoAccessBookingException;
import ru.practicum.server.booking.exception.UnsupportedStatusException;
import ru.practicum.server.booking.exception.WrongUserApproveException;
import ru.practicum.server.booking.mapper.BookingMapper;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.exception.UserNotFoundException;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = getUser(1);
        booker = getUser(2);
        item = getItem(owner);
        booking = getBooking();
    }

    @Test
    void save_whenUserIdNotValid_thenUserNotFoundExceptionThrow() {
        int userId = 0;

        doThrow(UserNotFoundException.class).when(userService).get(userId);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.save(userId, new BookingDto()));
    }

    @Test
    void save_whenDataValid_thenReturnedBookingDto() {
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        Booking booking = BookingMapper.toBooking(expectedBookingDto);

        when(userService.get(booker.getId())).thenReturn(UserMapper.toUserDto(booker));
        when(itemService.getAvailableItem(item.getId())).thenReturn(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.save(booker.getId(), expectedBookingDto);
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void approveBooking_whenInvokedWithTrue_ReturnedBookingDtoWithStatusApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemService.getAvailableItem(item.getId())).thenReturn(item);
        when(userService.get(owner.getId())).thenReturn(UserMapper.toUserDto(owner));

        BookingDto bookingDto = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        BookingStatus expectedStatus = BookingStatus.APPROVED;
        BookingStatus actualStatus = bookingDto.getStatus();

        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void approveBooking_whenStatusNotWaiting_thenUnsupportedStatusExceptionThrow() {
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));
    }

    @Test
    void approveBooking_whenApprovedOtherUser_thenWrongUserApproveExceptionThrow() {
        User otherUser = getUser(3);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemService.getAvailableItem(item.getId())).thenReturn(item);
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(otherUser));

        assertThrows(WrongUserApproveException.class,
                () -> bookingService.approveBooking(otherUser.getId(), booking.getId(), true));
    }

    @Test
    void approveBooking_whenBookerEqualsOwner_thenNoAccessBookingExceptionThrow() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(itemService.getAvailableItem(item.getId())).thenReturn(item);
        when(userService.get(booker.getId())).thenReturn(UserMapper.toUserDto(booker));

        assertThrows(NoAccessBookingException.class,
                () -> bookingService.approveBooking(booker.getId(), booking.getId(), true));
    }

    @Test
    void get_whenUserIdValid_thenReturnedBookingDto() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = bookingService.get(owner.getId(), booking.getId());
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        assertEquals(expectedBookingDto, actualBookingDto);
    }

    @Test
    void get_whenOtherUserId_thenNoAccessBookingExceptionThrow() {
        int otherUserId = 100;

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NoAccessBookingException.class,
                () -> bookingService.get(otherUserId, booking.getId()));
    }

    @Test
    void get_whenBookingIdNotValid_thenBookingNotFoundExceptionThrow() {
        long userId = 0;
        long wrongBookingId = 1;

        when(bookingRepository.findById(wrongBookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.get(userId, wrongBookingId));
    }

    @Test
    void getAll_whenOwnerAndStatusFuture_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.FUTURE;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository
                .findAllByItemOwnerIdAndStartAfter(
                        eq(owner.getId()), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusFuture_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.FUTURE;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerIdAndStartAfter(
                eq(owner.getId()), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusPast_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.PAST;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(
                eq(owner.getId()), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusPast_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.PAST;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerIdAndEndBefore(
                eq(owner.getId()), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusCurrent_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.CURRENT;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                eq(owner.getId()), any(), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusCurrent_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.CURRENT;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                eq(owner.getId()), any(), any(), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusWaiting_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.WAITING;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(
                eq(owner.getId()), eq(BookingStatus.WAITING), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusWaiting_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.WAITING;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerIdAndStatus(
                eq(owner.getId()), eq(BookingStatus.WAITING), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusRejected_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.REJECTED;
        boolean isOwner = true;
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerIdAndStatus(
                eq(owner.getId()), eq(BookingStatus.REJECTED), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusRejected_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.REJECTED;
        boolean isOwner = false;
        booking.setStatus(BookingStatus.REJECTED);
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerIdAndStatus(
                eq(owner.getId()), eq(BookingStatus.REJECTED), eq(getPageRequest())))
                .thenReturn(bookingList);

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusAll_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.ALL;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerId(
                eq(owner.getId()), eq(getPageRequest())))
                .thenReturn(new PageImpl<>(bookingList, getPageRequest(), 3));

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenOwnerAndStatusAll_thenReturnedCollectionBookingDto2() {
        BookingState state = BookingState.ALL;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByItemOwnerId(
                owner.getId(), PageRequest.of(4, 1)))
                .thenReturn(new PageImpl<>(bookingList, PageRequest.of(4, 1), 1));

        List<BookingDto> actualList = bookingService
                .getAll(owner.getId(), state, isOwner, PageRequest.of(4, 1));
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenBookerAndStatusAll_thenReturnedCollectionBookingDto() {
        BookingState state = BookingState.ALL;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findAllByBookerId(
                eq(booker.getId()), eq(getPageRequest())))
                .thenReturn(new PageImpl<>(bookingList, getPageRequest(), 0));

        List<BookingDto> actualList = bookingService
                .getAll(booker.getId(), state, isOwner, getPageRequest());
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenExcessPage_thenReturnedLastPage() {
        BookingState state = BookingState.ALL;
        boolean isOwner = false;
        List<Booking> bookingList = List.of(booking, getBooking(), getBooking());

        when(bookingRepository.findAllByBookerId(
                eq(booker.getId()), eq(PageRequest.of(3, 1))))
                .thenReturn(new PageImpl<>(bookingList, PageRequest.of(1, 1), 2));

        when(bookingRepository.findAllByBookerId(
                eq(booker.getId()), eq(PageRequest.of(1, 1))))
                .thenReturn(new PageImpl<>(bookingList, PageRequest.of(1, 1), 2));

        List<BookingDto> actualList = bookingService
                .getAll(booker.getId(), state, isOwner, PageRequest.of(3, 1));
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll_whenExcessPageFromOwner_thenReturnedLastPage() {
        BookingState state = BookingState.ALL;
        boolean isOwner = true;
        List<Booking> bookingList = List.of(booking, getBooking(), getBooking());

        when(bookingRepository.findAllByItemOwnerId(
                eq(booker.getId()), eq(PageRequest.of(3, 1))))
                .thenReturn(new PageImpl<>(bookingList, PageRequest.of(1, 1), 2));

        when(bookingRepository.findAllByItemOwnerId(
                eq(booker.getId()), eq(PageRequest.of(1, 1))))
                .thenReturn(new PageImpl<>(bookingList, PageRequest.of(1, 1), 2));

        List<BookingDto> actualList = bookingService
                .getAll(booker.getId(), state, isOwner, PageRequest.of(3, 1));
        List<BookingDto> expectedList = bookingList.stream()
                .map(BookingMapper::toBookingDto).collect(Collectors.toList());

        assertEquals(expectedList, actualList);
    }

    private User getUser(int id) {
        User user = new User();
        user.setId(id);
        user.setName("John" + id);
        user.setEmail("john" + id + "@ya.ru");
        return user;
    }

    private Item getItem(User owner) {
        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    private Booking getBooking() {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusSeconds(300));
        booking.setEnd(LocalDateTime.now().plusSeconds(600));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private PageRequest getPageRequest() {
        return PageRequest.of(0, 10);
    }
}