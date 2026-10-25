package ua.edu.ukma.unischeduler.feature.classoverride;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
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

    @Override
    public ClassOverrideResponseDto create(ClassOverrideCreateDto dto) {
        ClassOverride classOverride = classOverrideMapper.toEntity(dto);
        if (!userRepository.existsById(dto.getUser()))
            throw new ResourceNotFoundException("User not found");
        CourseClass courseClass = courseClassRepository.findById(dto.getCourseClass())
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        classOverride.setCourseClass(courseClass);
        ClassOverride saved = classOverrideRepository.save(classOverride);
        return classOverrideMapper.toDto(saved);
    }

    @Override
    public ClassOverrideResponseDto findById(Long id) {
        ClassOverride found = classOverrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        return classOverrideMapper.toDto(found);
    }

    @Override
    public List<ClassOverrideResponseDto> findAll() {
        return classOverrideRepository.findAll()
                .stream()
                .map(classOverrideMapper::toDto)
                .toList();
    }

    @Override
    public ClassOverrideResponseDto update(ClassOverrideUpdateDto dto) {
        ClassOverride found = classOverrideRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Class override not found"));
        classOverrideMapper.update(dto, found);
        ClassOverride saved = classOverrideRepository.save(found);
        return classOverrideMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!classOverrideRepository.existsById(id))
            throw new ResourceNotFoundException("Class override not found");
        classOverrideRepository.deleteById(id);
    }
}
