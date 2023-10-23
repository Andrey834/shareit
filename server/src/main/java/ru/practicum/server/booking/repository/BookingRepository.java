package ru.practicum.server.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerId(long ownerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(
            long ownerId,
            LocalDateTime presentForStart,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfter(
            long ownerId,
            LocalDateTime presentForStart,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndStartAfter(
            long bookerId,
            LocalDateTime presentForStart,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStartAfter(
            long ownerId,
            LocalDateTime presentForStart,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndEndBefore(
            long bookerId,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndEndBefore(
            long ownerId,
            LocalDateTime presentForEnd,
            Pageable pageable
    );

    List<Booking> findAllByItemOwnerIdAndStatus(
            long ownerId,
            BookingStatus status,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndStatus(
            long bookerId,
            BookingStatus status,
            Pageable pageable
    );

    List<Booking> findAllByBookerIdAndEndBeforeAndStatus(
            long bookerId,
            LocalDateTime present,
            BookingStatus status
    );

    List<Booking> findBookingByItemIdAndItemOwnerIdAndStatus(
            long itemId,
            long ownerId,
            BookingStatus status
    );
}
