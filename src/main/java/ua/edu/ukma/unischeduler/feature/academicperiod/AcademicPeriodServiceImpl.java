package ua.edu.ukma.unischeduler.feature.academicperiod;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AcademicPeriodServiceImpl implements AcademicPeriodService{
    private final AcademicPeriodRepository repository;
    private final AcademicPeriodMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public AcademicPeriodResponseDto getCurrent() {
        AcademicPeriod period = repository.findById(AcademicPeriod.SINGLETON_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Academic period is not configured"));
        return mapper.toDto(period, calculateWeek(period.getStartDate()));
    }

    @Override
    @Transactional
    public AcademicPeriodResponseDto update(AcademicPeriodUpdateDto request) {
        AcademicPeriod period = repository.findById(AcademicPeriod.SINGLETON_ID)
                .orElseGet(() -> AcademicPeriod.builder().build());
        mapper.update(request, period);
        AcademicPeriod saved = repository.save(period);
        return mapper.toDto(saved, calculateWeek(saved.getStartDate()));
    }

    private int calculateWeek(LocalDate startDate) {
        LocalDate today = LocalDate.now(ZoneId.of("Europe/Kyiv"));
        if (today.isBefore(startDate)) {
            return 0;
        }
        return (int) ChronoUnit.WEEKS.between(startDate, today) + 1;
    }
}
