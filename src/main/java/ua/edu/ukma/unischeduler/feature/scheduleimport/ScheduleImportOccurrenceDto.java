package ua.edu.ukma.unischeduler.feature.scheduleimport;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ScheduleImportOccurrenceDto(
        @NotBlank @Size(max = 64) String sourceKey,
        @NotBlank @Size(max = 100) String courseName,
        @Size(max = 100) String teacher,
        @Size(max = 50) String group,
        @Size(max = 100) String location,
        @Size(max = 1000) String description,
        @NotNull LocalDateTime startsAt,
        @NotNull LocalDateTime endsAt,
        Integer sourceRow,
        @Size(max = 12000) String sourceMetadata
) {}
