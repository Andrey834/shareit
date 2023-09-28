package ru.practicum.shareit.booking.repository;

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
    List<Booking> findBookingByItemIdAndItemOwnerIdAndStatus(Integer itemId, Integer ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer ownerId,
            LocalDateTime present,
            LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer ownerId,
            LocalDateTime present,
            LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime present);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime present);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime present);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime present);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeAndStatus(Integer bookerId, LocalDateTime present, BookingStatus status);
}
