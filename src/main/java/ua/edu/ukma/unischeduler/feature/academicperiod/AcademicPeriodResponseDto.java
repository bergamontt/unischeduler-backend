package ua.edu.ukma.unischeduler.feature.academicperiod;

import ua.edu.ukma.unischeduler.common.enums.Semester;

import java.time.LocalDate;

public record AcademicPeriodResponseDto(
        Semester semester,
        LocalDate startDate,
        int currentWeek
) {}