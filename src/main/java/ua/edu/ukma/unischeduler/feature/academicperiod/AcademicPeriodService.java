package ua.edu.ukma.unischeduler.feature.academicperiod;

public interface AcademicPeriodService {
    AcademicPeriodResponseDto getCurrent();
    AcademicPeriodResponseDto update(AcademicPeriodUpdateDto request);
}
