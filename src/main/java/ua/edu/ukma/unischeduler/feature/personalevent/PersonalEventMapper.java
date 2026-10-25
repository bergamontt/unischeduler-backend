package ua.edu.ukma.unischeduler.feature.personalevent;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonalEventMapper {
    PersonalEventResponseDto toDto(PersonalEvent personalEvent);

    @Mapping(target = "id", ignore = true)
    PersonalEvent toEntity(PersonalEventCreateDto dto);

    @Mapping(target = "id", ignore = true)
    void update(PersonalEventUpdateDto dto, @MappingTarget PersonalEvent personalEvent);
}
