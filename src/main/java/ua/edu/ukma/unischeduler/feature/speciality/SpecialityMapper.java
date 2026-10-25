package ua.edu.ukma.unischeduler.feature.speciality;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialityMapper {
    SpecialityResponseDto toDto(Speciality speciality);

    @Mapping(target = "id", ignore = true)
    Speciality toEntity(SpecialityCreateDto dto);

    @Mapping(target = "id", ignore = true)
    void update(SpecialityUpdateDto dto, @MappingTarget Speciality speciality);
}
