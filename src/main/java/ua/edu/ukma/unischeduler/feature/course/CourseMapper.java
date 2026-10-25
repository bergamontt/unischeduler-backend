package ua.edu.ukma.unischeduler.feature.course;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponseDto toDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialities", ignore = true)
    Course toEntity(CourseCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialities", ignore = true)
    void update(CourseUpdateDto dto, @MappingTarget Course course);
}
