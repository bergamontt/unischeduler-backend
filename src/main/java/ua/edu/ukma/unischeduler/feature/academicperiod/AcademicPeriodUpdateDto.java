package ua.edu.ukma.unischeduler.feature.academicperiod;

import jakarta.validation.constraints.NotNull;
import ua.edu.ukma.unischeduler.common.enums.Semester;

import java.time.LocalDate;

public record AcademicPeriodUpdateDto(
        @NotNull Semester semester,
        @NotNull LocalDate startDate
) {}
