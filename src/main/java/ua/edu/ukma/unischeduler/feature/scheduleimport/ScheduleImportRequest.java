package ua.edu.ukma.unischeduler.feature.scheduleimport;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import ua.edu.ukma.unischeduler.common.enums.Semester;
import java.util.List;

public record ScheduleImportRequest(
        @NotBlank @Size(max = 100) String specialityName,
        @NotBlank @Size(max = 100) String faculty,
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Pattern(regexp = "20\\d{2}-20\\d{2}") String academicYear,
        @NotNull @Positive Integer studyYear,
        @NotNull Semester semester,
        @NotEmpty List<@NotNull @Valid ScheduleImportOccurrenceDto> occurrences
) {}
