package ua.edu.ukma.unischeduler.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final SpecialityRepository specialityRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        Speciality speciality = specialityRepository.findById(dto.getSpecialityId())
                .orElseThrow(() -> new ResourceNotFoundException("Speciality not found"));
        user.setSpeciality(speciality);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto update(UserUpdateDto dto) {
        User found = userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Speciality speciality = specialityRepository.findById(dto.getSpecialityId())
                .orElseThrow(() -> new ResourceNotFoundException("Speciality not found"));
        found.setSpeciality(speciality);
        userMapper.update(dto, found);
        User saved = userRepository.save(found);
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (currentUserService.getCurrentUser().getId().equals(id)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User not found");
        userRepository.deleteById(id);
    }
}
