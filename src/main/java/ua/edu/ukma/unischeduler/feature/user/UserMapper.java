package ua.edu.ukma.unischeduler.feature.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "speciality", ignore = true)
    User toEntity(UserCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "speciality", ignore = true)
    void update(UserUpdateDto dto, @MappingTarget User user);
}
