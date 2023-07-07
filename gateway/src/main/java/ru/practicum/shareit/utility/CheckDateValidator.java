package ru.practicum.shareit.utility;

import ru.practicum.shareit.booking.model.dto.BookingDtoIn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingDtoIn> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoIn bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        if (start.isBefore(LocalDateTime.now())) {
            return false;
        }
        return start.isBefore(end);
    }
}
