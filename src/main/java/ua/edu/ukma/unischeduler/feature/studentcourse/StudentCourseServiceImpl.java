package ua.edu.ukma.unischeduler.feature.studentcourse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.course.CourseMapper;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentCourseServiceImpl implements StudentCourseService {
    private final StudentCourseRepository repository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StudentCourseResponseDto> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public StudentCourseResponseDto save(StudentCourseCreateDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + dto.getCourseId()));

        StudentCourse entity = repository.findByUserIdAndCourseId(dto.getUserId(), dto.getCourseId())
                .orElseGet(() -> StudentCourse.builder()
                        .userId(dto.getUserId())
                        .course(course)
                        .build());

        if (dto.getGroup() != null) {
            entity.setGroup(dto.getGroup().trim());
        } else if (entity.getGroup() == null) {
            entity.setGroup("1");
        }

        StudentCourse saved = repository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long courseId) {
        repository.deleteByUserIdAndCourseId(userId, courseId);
    }

    private StudentCourseResponseDto toDto(StudentCourse entity) {
        return StudentCourseResponseDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .courseId(entity.getCourse().getId())
                .course(courseMapper.toDto(entity.getCourse()))
                .group(entity.getGroup())
                .build();
    }
}
