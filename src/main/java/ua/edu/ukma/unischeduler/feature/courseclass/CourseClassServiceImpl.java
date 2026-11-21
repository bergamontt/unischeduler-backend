package ua.edu.ukma.unischeduler.feature.courseclass;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.common.validation.TimeRangeValidator;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverrideRepository;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseClassServiceImpl implements CourseClassService {
    private final CourseRepository courseRepository;
    private final CourseClassRepository courseClassRepository;
    private final ClassOverrideRepository classOverrideRepository;
    private final CourseClassMapper courseClassMapper;

    @Override
    @Transactional
    public CourseClassResponseDto create(CourseClassCreateDto dto) {
        TimeRangeValidator.validate(dto.getStartsAt(), dto.getEndsAt());
        Course course = courseRepository.findById(dto.getCourse())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        CourseClass courseClass = courseClassMapper.toEntity(dto);
        courseClass.setCourse(course);
        CourseClass created = courseClassRepository.save(courseClass);
        return courseClassMapper.toDto(created);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseClassResponseDto findById(Long id) {
        CourseClass found = courseClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
        return courseClassMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseClassResponseDto> findAll() {
        return courseClassRepository.findAll()
                .stream()
                .map(courseClassMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CourseClassResponseDto update(CourseClassUpdateDto dto) {
        TimeRangeValidator.validate(dto.getStartsAt(), dto.getEndsAt());
        CourseClass found = courseClassRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
        Course course = courseRepository.findById(dto.getCourse())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        found.setCourse(course);
        courseClassMapper.update(dto, found);
        CourseClass saved = courseClassRepository.save(found);
        return courseClassMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!courseClassRepository.existsById(id))
            throw new ResourceNotFoundException("Course class not found");
        classOverrideRepository.deleteAllByCourseClassId(id);
        courseClassRepository.deleteById(id);
    }
}
