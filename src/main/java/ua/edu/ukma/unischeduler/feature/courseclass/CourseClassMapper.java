package ua.edu.ukma.unischeduler.feature.courseclass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseClassMapper {
    CourseClassResponseDto toDto(CourseClass courseClass);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    CourseClass toEntity(CourseClassCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    void update(CourseClassUpdateDto dto, @MappingTarget CourseClass courseClass);
}
