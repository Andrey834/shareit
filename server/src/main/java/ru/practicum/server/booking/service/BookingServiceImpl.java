package ru.practicum.server.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

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
    public BookingDto save(long userId, BookingDto bookingDto) {
        UserDto userDto = userService.get(userId);
        Item item = itemService.getAvailableItem(bookingDto.getItemId());

        if (item.getOwner().getId() == userId) throw new NoAccessBookingException("This is your item");

        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(userDto);
        bookingDto.setItem(ItemMapper.toItemDto(item));

        Booking booking = BookingMapper.toBooking(bookingDto);
        Booking newBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new UnsupportedStatusException("Booking is " + booking.getStatus().getValue());
        }

        UserDto user = userService.get(userId);
        long ownerId = itemService.getAvailableItem(booking.getItem().getId()).getOwner().getId();

        if (ownerId == user.getId()) {
            BookingStatus status = BookingStatus.REJECTED;
            if (approved) status = BookingStatus.APPROVED;
            booking.setStatus(status);

            bookingRepository.save(booking);
        } else if (booking.getBooker().getId() == userId) {
            throw new NoAccessBookingException("Only the owner can change the status");
        } else {
            throw new WrongUserApproveException("User № " + userId + " does not have access");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto get(long userId, long bookingId) {
        userService.get(userId);

        Booking booking = getBooking(bookingId);
        long ownerId = booking.getItem().getOwner().getId();
        long bookerId = booking.getBooker().getId();

        if (ownerId == userId || bookerId == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NoAccessBookingException("Access is denied");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAll(long userId, BookingState state, boolean isOwner, Pageable pageable) {
        userService.get(userId);
        List<Booking> bookingList;

        switch (state) {
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

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Booking with ID:" + bookingId + " not found"));
    }
}