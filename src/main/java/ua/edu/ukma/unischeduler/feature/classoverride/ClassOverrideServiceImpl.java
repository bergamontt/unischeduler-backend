package ua.edu.ukma.unischeduler.feature.classoverride;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassRepository;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassOverrideServiceImpl implements ClassOverrideService {
    private final UserRepository userRepository;
    private final CourseClassRepository courseClassRepository;
    private final ClassOverrideRepository classOverrideRepository;
    private final ClassOverrideMapper classOverrideMapper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ClassOverrideResponseDto create(ClassOverrideCreateDto dto) {
        Long userId = currentUserService.resolveUserId(dto.getUser());
        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found");
        CourseClass courseClass = courseClassRepository.findById(dto.getCourseClass())
                .orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
        ClassOverride classOverride = classOverrideMapper.toEntity(dto);
        classOverride.setUser(userId);
        classOverride.setCourseClass(courseClass);
        ClassOverride saved = classOverrideRepository.save(classOverride);
        return classOverrideMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassOverrideResponseDto findById(Long id) {
        ClassOverride found = classOverrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        currentUserService.verifyOwnership(found.getUser());
        return classOverrideMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassOverrideResponseDto> findAll() {
        List<ClassOverride> overrides = currentUserService.isAdmin()
                ? classOverrideRepository.findAll()
                : classOverrideRepository.findAllByUser(currentUserService.getCurrentUser().getId());
        return overrides.stream()
                .map(classOverrideMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ClassOverrideResponseDto update(ClassOverrideUpdateDto dto) {
        ClassOverride found = classOverrideRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        currentUserService.verifyOwnership(found.getUser());
        classOverrideMapper.update(dto, found);
        ClassOverride saved = classOverrideRepository.save(found);
        return classOverrideMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClassOverride found = classOverrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        currentUserService.verifyOwnership(found.getUser());
        classOverrideRepository.delete(found);
    }
}
