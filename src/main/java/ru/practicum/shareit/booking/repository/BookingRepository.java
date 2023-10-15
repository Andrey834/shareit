package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByBookerId(Integer bookerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(Integer ownerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            Integer ownerId,
            LocalDateTime presentForStart,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            Integer ownerId,
            LocalDateTime presentForStart,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndStartAfter(
            Integer bookerId,
            LocalDateTime presentForStart,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStartAfter(
            Integer ownerId,
            LocalDateTime presentForStart,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndEndBefore(
            Integer bookerId,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndEndBefore(
            Integer ownerId,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStatus(
            Integer ownerId,
            BookingStatus status,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndStatus(
            Integer bookerId,
            BookingStatus status,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndEndBeforeAndStatus(
            Integer bookerId,
            LocalDateTime present,
            BookingStatus status
    );

    List<Booking> findBookingByItemIdAndItemOwnerIdAndStatus(
            Integer itemId,
            Integer ownerId,
            BookingStatus status
    );
}
