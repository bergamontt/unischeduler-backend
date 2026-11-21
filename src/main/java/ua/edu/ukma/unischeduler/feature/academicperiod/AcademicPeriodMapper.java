package ua.edu.ukma.unischeduler.feature.academicperiod;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AcademicPeriodMapper {
    AcademicPeriodResponseDto toDto(AcademicPeriod entity, int currentWeek);

    @Mapping(target = "id", ignore = true)
    void update(AcademicPeriodUpdateDto request, @MappingTarget AcademicPeriod entity);
}
