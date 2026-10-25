package ua.edu.ukma.unischeduler.feature.courseclass;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.course.CourseMapper;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseClassServiceImpl implements CourseClassService {
    private final CourseRepository courseRepository;
    private final CourseClassRepository courseClassRepository;
    private final CourseClassMapper courseClassMapper;

    @Override
    public CourseClassResponseDto create(CourseClassCreateDto dto) {
        CourseClass courseClass = courseClassMapper.toEntity(dto);
        CourseClass created = courseClassRepository.save(courseClass);
        return courseClassMapper.toDto(created);
    }

    @Override
    public CourseClassResponseDto findById(Long id) {
        CourseClass found = courseClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course class not found"));
        return courseClassMapper.toDto(found);
    }

    @Override
    public List<CourseClassResponseDto> findAll() {
        return courseClassRepository.findAll()
                .stream()
                .map(courseClassMapper::toDto)
                .toList();
    }

    @Override
    public CourseClassResponseDto update(CourseClassUpdateDto dto) {
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
    public void delete(Long id) {
        if (!courseClassRepository.existsById(id))
            throw new ResourceNotFoundException("Course class not found");
        courseClassRepository.deleteById(id);
    }
}
