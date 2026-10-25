package ua.edu.ukma.unischeduler.feature.user;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserCreateDto dto);
    UserResponseDto findById(Long id);
    List<UserResponseDto> findAll();
    UserResponseDto update(UserUpdateDto dto);
    void delete(Long id);
}
