package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.item.BadDataItemException;
import ru.practicum.shareit.exception.item.ItemNotFoundException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.booking.WrongOwnerItemException;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public ItemDto create(int userId, ItemDto itemDto) {
        checkDataItem(itemDto);
        UserDto userDto = userService.get(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));
        Item newItem = itemRepository.save(item);

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(int userId, ItemDto itemDto, int itemId) {
        Item oldItem = getItemOwner(itemId, userId);
        changeItemData(itemDto, oldItem);
        Item newItem = itemRepository.save(oldItem);

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto get(Integer userId, Integer itemId) {
        checkUser(userId);

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Item with ID:" + itemId + "not found")
        );

        ItemDto itemDto = ItemMapper.toItemDto(item);

        setLastNextBooking(itemDto, userId);

        List<CommentDto> comments = commentRepository.findAllByItemId_Id(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(int userId) {
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> setLastNextBooking(itemDto, userId))
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(int userId, String strSearch) {
        checkUser(userId);
        if (strSearch.isBlank()) return new ArrayList<>();

        List<Item> items = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        strSearch, strSearch
                );

        return ItemMapper.listToItemDto(items);
    }

    @Override
    public CommentDto addComment(Integer itemId, Integer userId, Comment comment) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found № " + userId)
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Item not found № " + itemId)
        );

        List<Booking> bookingList = bookingRepository.findAllByBookerIdAndEndBeforeAndStatus(
                userId,
                LocalDateTime.now(),
                BookingStatus.APPROVED
        );

        Optional<Booking> booking = bookingList.stream()
                .filter(book -> book.getItem().getId().equals(itemId))
                .findFirst();

        if (booking.isPresent()) {
            comment.setAuthorId(user);
            comment.setItemId(item);
            comment.setCreated(LocalDateTime.now());
            Comment newComment = commentRepository.save(comment);
            return CommentMapper.toCommentDto(newComment);
        } else {
            throw new BadDataItemException("Comment can be added after using the item");
        }
    }

    private void checkDataItem(ItemDto itemDto) {
        final String itemName = itemDto.getName();
        final String itemDesc = itemDto.getDescription();
        final Boolean itemAvailable = itemDto.getAvailable();

        if (itemName == null || itemName.isBlank() || itemName.isEmpty()) {
            throw new BadDataItemException("item name not specified");
        } else if (itemDesc == null || itemDesc.isBlank() || itemDesc.isEmpty()) {
            throw new BadDataItemException("item description not specified");
        } else if (itemAvailable == null) {
            throw new BadDataItemException("item available not specified");
        }
    }

    private void setLastNextBooking(ItemDto itemDto, Integer userId) {
        List<Booking> bookingList = bookingRepository.findBookingByItemIdAndItemOwnerIdAndStatus(
                itemDto.getId(),
                userId,
                BookingStatus.APPROVED
        );

        BookingItemDto nextBooking = bookingList.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .map(ItemMapper::toBookingItemDto)
                .min(Comparator.comparing(BookingItemDto::getStart))
                .orElse(null);

        BookingItemDto lastBooking = bookingList.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .map(ItemMapper::toBookingItemDto)
                .max(Comparator.comparing(BookingItemDto::getEnd))
                .orElse(null);

        itemDto.setNextBooking(nextBooking);
        itemDto.setLastBooking(lastBooking);
    }

    private void changeItemData(ItemDto itemDto, Item oldItem) {
        if (itemDto.getName() != null) oldItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) oldItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) oldItem.setAvailable(itemDto.getAvailable());
    }

    private Item getItemOwner(int itemId, int userId) {
        return itemRepository.findByOwnerIdAndIdIs(userId, itemId).orElseThrow(
                () -> new WrongOwnerItemException("The user with ID:" + userId + " is not the owner")
        );
    }

    private void checkUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User with ID:" + userId + " not found");
        }
    }
}
