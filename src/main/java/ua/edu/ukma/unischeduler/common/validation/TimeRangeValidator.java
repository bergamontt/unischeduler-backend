package ua.edu.ukma.unischeduler.common.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeRangeValidator {
    public static void validate(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt != null && endsAt != null && !startsAt.isBefore(endsAt)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}
