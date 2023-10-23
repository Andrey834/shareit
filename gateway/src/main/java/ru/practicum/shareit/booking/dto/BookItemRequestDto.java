package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.exception.BadDataBookingException;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookItemRequestDto {
	private long itemId;
	@FutureOrPresent
	private LocalDateTime start;
	@Future
	private LocalDateTime end;

	public void checkTime() {
		LocalDateTime start = this.start;
		LocalDateTime end = this.end;

		if (start == null || end == null) throw new BadDataBookingException("start or end is null");
		if (end.isBefore(LocalDateTime.now())) throw new BadDataBookingException("end in past tense");
		if (end.isBefore(start)) throw new BadDataBookingException("end before start");
		if (start.isEqual(end)) throw new BadDataBookingException("start equal end");
		if (start.isBefore(LocalDateTime.now())) throw new BadDataBookingException("start in past tense");
	}
}
