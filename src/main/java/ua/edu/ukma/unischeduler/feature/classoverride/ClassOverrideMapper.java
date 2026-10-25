package ua.edu.ukma.unischeduler.feature.classoverride;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClassOverrideMapper {
    ClassOverrideResponseDto toDto(ClassOverride entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseClass", ignore = true)
    ClassOverride toEntity(ClassOverrideCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "courseClass", ignore = true)
    void update(ClassOverrideUpdateDto dto, @MappingTarget ClassOverride entity);
}
