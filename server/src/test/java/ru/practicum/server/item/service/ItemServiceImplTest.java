package ru.practicum.server.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.booking.exception.WrongOwnerItemException;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.exception.BadDataItemException;
import ru.practicum.server.item.exception.ItemNotAvailableException;
import ru.practicum.server.item.exception.ItemNotFoundException;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.CommentRepository;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.service.RequestServiceImpl;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestServiceImpl requestService;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = getUser(1);
        item = getItem();
        booking = getBooking();
    }

    @Test
    void create_whenDataIsValid_thenReturnedItemDto() {
        when(userService.get(owner.getId())).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItemDto = itemService.create(owner.getId(), ItemMapper.toItemDto(item));

        assertEquals(ItemMapper.toItemDto(item), actualItemDto);
    }

    @Test
    void create_whenDataIsValid_thenReturnedItemDtoWithRequestId() {
        item.setRequestId(1);

        when(userService.get(owner.getId())).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.save(item)).thenReturn(item);
        when(requestService.existsById(item.getRequestId())).thenReturn(true);

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        ItemDto actualItemDto = itemService.create(owner.getId(), ItemMapper.toItemDto(item));
        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void update_whenDataValid_thenReturnedItemDto() {
        Item newItem = getItem();
        newItem.setName("new");
        newItem.setDescription("new desc");

        when(itemRepository.findByOwnerIdAndIdIs(owner.getId(), item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(newItem)).thenReturn(newItem);

        itemService.update(owner.getId(), ItemMapper.toItemDto(newItem), item.getId());
        verify(itemRepository).save(itemArgumentCaptor.capture());

        Item expectedItem = itemArgumentCaptor.getValue();
        assertEquals("new", expectedItem.getName());
        assertEquals("new desc", expectedItem.getDescription());
    }

    @Test
    void update_whenOwnerIdNotValid_thenWrongOwnerItemExceptionThrow() {
        int wrongOwnerId = 2;

        assertThrows(WrongOwnerItemException.class,
                () -> itemService.update(wrongOwnerId, ItemMapper.toItemDto(item), item.getId()));
    }

    @Test
    void get_whenItemIdNotValid_thenItemNotFoundExceptionThrow() {
        long itemId = 0;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.get(owner.getId(), itemId));
    }

    @Test
    void get_whenItemIdValid_thenReturnedItemDto() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository
                .findBookingByItemIdAndItemOwnerIdAndStatus(
                        item.getId(),
                        owner.getId(),
                        BookingStatus.APPROVED)
        ).thenReturn(List.of(booking));

        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setNextBooking(ItemMapper.toBookingItemDto(booking));
        ItemDto actualItemDto = itemService.get(owner.getId(), item.getId());
        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void getAll_whenInvoked_thenReturnedCollectionItemDto() {
        Page<Item> pageList = new PageImpl<>(List.of(item));
        PageRequest pageable = PageRequest.of(0, 10);

        when(itemRepository.findAllByOwnerId(owner.getId(), pageable)).thenReturn(pageList);

        List<ItemDto> expectedList = ItemMapper.listToItemDto(pageList.toList());
        List<ItemDto> actualList = itemService.getAll(owner.getId(), pageable);

        assertEquals(expectedList, actualList);
    }

    @Test
    void search_whenTextIsBlank_ReturnedEmptyList() {
        PageRequest pageable = PageRequest.of(0, 10);
        String text = "";

        List<ItemDto> expectedList = new ArrayList<>();
        List<ItemDto> actualList = itemService.search(owner.getId(), text, pageable);

        assertEquals(expectedList, actualList);
    }

    @Test
    void search_whenTextValid_ReturnedCollectionItemDto() {
        PageRequest pageable = PageRequest.of(0, 10);
        String text = "new";

        when(itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        text, text, pageable)
        ).thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> expectedList = List.of(ItemMapper.toItemDto(item));
        List<ItemDto> actualList = itemService.search(owner.getId(), text, pageable);

        assertEquals(expectedList, actualList);
    }

    @Test
    void addComment_whenItemIdNotValid_thenItemNotFoundExceptionThrow() {
        long itemId = 999;
        long userId = 999;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(itemId, userId, new Comment()));

    }

    @Test
    void addComment_whenDataValid_thenReturnedCommentDto() {
        Comment comment = new Comment();
        comment.setAuthorId(getUser(2));

        when(userService.get(owner.getId())).thenReturn(UserMapper.toUserDto(owner));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(bookingRepository
                .findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any())
        )
                .thenReturn(List.of(booking));

        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto actualCommentDto = itemService.addComment(item.getId(), owner.getId(), comment);
        CommentDto expectedCommentDto = CommentMapper.toCommentDto(comment);

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void addComment_whenNotUsingUser_thenBadDataItemExceptionThrow() {
        Comment comment = new Comment();
        comment.setAuthorId(owner);
        item.setOwner(owner);

        when(userService.get(owner.getId())).thenReturn(UserMapper.toUserDto(owner));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        when(bookingRepository
                .findAllByBookerIdAndEndBeforeAndStatus(anyLong(), any(), any())
        )
                .thenReturn(List.of());

        assertThrows(BadDataItemException.class,
                () -> itemService.addComment(item.getId(), owner.getId(), comment));
    }

    @Test
    void getAvailableItem_whenItemIdValid_thenReturnedItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Item actualItem = itemService.getAvailableItem(item.getId());

        assertEquals(item, actualItem);
    }

    @Test
    void getAvailableItem_whenItemIdNotValid_thenItemNotFoundExceptionThrow() {
        long itemId = 0;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getAvailableItem(itemId));
    }

    @Test
    void getAvailableItem_whenAvailableFalse_thenItemNotAvailableExceptionThrow() {
        item.setAvailable(false);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class,
                () -> itemService.getAvailableItem(item.getId()));
    }

    private User getUser(int id) {
        User user = new User();
        user.setId(id);
        user.setName("John" + id);
        user.setEmail("john" + id + "@ya.ru");
        return user;
    }

    private Item getItem() {
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
        booking.setBooker(getUser(3));
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusSeconds(300));
        booking.setEnd(LocalDateTime.now().plusSeconds(600));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}