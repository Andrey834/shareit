package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.booking.BadDataBookingException;
import ru.practicum.shareit.exception.booking.BookingNotFoundException;
import ru.practicum.shareit.exception.booking.WrongUserApproveException;
import ru.practicum.shareit.exception.booking.NoAccessBookingException;
import ru.practicum.shareit.exception.booking.UnsupportedStatusException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto save(Integer userId, BookingDto bookingDto) {
        UserDto userDto = userService.get(userId);
        Item item = itemService.getAvailableItem(bookingDto.getItemId());

        checkDataForBooking(bookingDto, item, userId);

        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(ItemMapper.toItemDto(item));

        Booking booking = BookingMapper.toBooking(bookingDto);
        Booking newBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto approveBooking(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new UnsupportedStatusException("Booking is " + booking.getStatus().getValue());
        }

        UserDto user = userService.get(userId);
        Integer ownerId = itemService.getAvailableItem(booking.getItem().getId()).getOwner().getId();

        if (ownerId.equals(user.getId())) {
            BookingStatus status = BookingStatus.REJECTED;
            if (approved) status = BookingStatus.APPROVED;
            booking.setStatus(status);

            bookingRepository.save(booking);
        } else if (booking.getBooker().getId().equals(userId)) {
            throw new NoAccessBookingException("Only the owner can change the status");
        } else {
            throw new WrongUserApproveException("User № " + userId + " does not have access");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto get(Integer userId, Integer bookingId) {
        userService.get(userId);

        Booking booking = getBooking(bookingId);
        Integer ownerId = booking.getItem().getOwner().getId();
        Integer bookerId = booking.getBooker().getId();

        if (ownerId.equals(userId) || bookerId.equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NoAccessBookingException("Access is denied");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAll(Integer userId, String state, boolean isOwner, Pageable pageable) {
        BookingState currentState;

        try {
            currentState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }

        userService.get(userId);
        List<Booking> bookingList;

        switch (currentState) {
            case FUTURE:
                if (isOwner) {
                    bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfter(
                            userId,
                            LocalDateTime.now(),
                            pageable
                    );
                } else {
                    bookingList = bookingRepository.findAllByBookerIdAndStartAfter(
                            userId,
                            LocalDateTime.now(),
                            pageable
                    );
                }
                break;
            case PAST:
                if (isOwner) {
                    bookingList = bookingRepository.findAllByItemOwnerIdAndEndBefore(
                            userId,
                            LocalDateTime.now(),
                            pageable
                    );
                } else {
                    bookingList = bookingRepository.findAllByBookerIdAndEndBefore(
                            userId,
                            LocalDateTime.now(),
                            pageable
                    );
                }
                break;
            case CURRENT:
                if (isOwner) {
                    bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            pageable
                    );
                } else {
                    bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            pageable
                    );
                }
                break;
            case WAITING:
                if (isOwner) {
                    bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(
                            userId,
                            BookingStatus.WAITING,
                            pageable
                    );
                } else {
                    bookingList = bookingRepository.findAllByBookerIdAndStatus(
                            userId,
                            BookingStatus.WAITING,
                            pageable
                    );
                }
                break;
            case REJECTED:
                if (isOwner) {
                    bookingList = bookingRepository.findAllByItemOwnerIdAndStatus(
                            userId,
                            BookingStatus.REJECTED,
                            pageable
                    );
                } else {
                    bookingList = bookingRepository.findAllByBookerIdAndStatus(
                            userId,
                            BookingStatus.REJECTED,
                            pageable
                    );
                }
                break;
            default:
                if (isOwner) {
                    Page<Booking> pages = bookingRepository.findAllByItemOwnerId(userId, pageable);
                    Optional<PageRequest> editPageable = getLastPage(pageable, pages);

                    bookingList = editPageable.map(pageRequest -> bookingRepository
                                    .findAllByItemOwnerId(userId, pageRequest)
                                    .toList())
                            .orElseGet(pages::toList);
                } else {
                    Page<Booking> pages = bookingRepository.findAllByBookerId(userId, pageable);
                    Optional<PageRequest> editPageable = getLastPage(pageable, pages);

                    bookingList = editPageable.map(pageRequest -> bookingRepository
                                    .findAllByBookerId(userId, pageRequest)
                                    .toList())
                            .orElseGet(pages::toList);
                }
                break;
        }

        return bookingList.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

    }

    private Optional<PageRequest> getLastPage(Pageable pageable, Page<Booking> pages) {
        int totalPages = pages.getTotalPages();
        int currentPages = pageable.getPageNumber();
        if (totalPages < currentPages) {
            return Optional.of(PageRequest.of(totalPages - 1, pageable.getPageSize(), pageable.getSort()));
        }
        return Optional.empty();
    }

    private Booking getBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Booking with ID:" + bookingId + " not found")
        );
    }

    private void checkDataForBooking(BookingDto bookingDto, Item item, Integer userId) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        Integer ownerId = item.getOwner().getId();

        if (ownerId.equals(userId)) throw new NoAccessBookingException("This is your item");
        if (start == null || end == null) throw new BadDataBookingException("start or end is null");
        if (end.isBefore(LocalDateTime.now())) throw new BadDataBookingException("end in past tense");
        if (end.isBefore(start)) throw new BadDataBookingException("end before start");
        if (start.isEqual(end)) throw new BadDataBookingException("start equal end");
        if (start.isBefore(LocalDateTime.now())) throw new BadDataBookingException("start in past tense");
    }
}