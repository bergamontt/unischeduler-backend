package ua.edu.ukma.unischeduler.feature.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverrideRepository;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassRepository;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityRepository;
import ua.edu.ukma.unischeduler.feature.studentcourse.StudentCourseRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final SpecialityRepository specialityRepository;
    private final CourseRepository courseRepository;
    private final CourseClassRepository courseClassRepository;
    private final ClassOverrideRepository classOverrideRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponseDto create(CourseCreateDto dto) {
        Course course = courseMapper.toEntity(dto);
        Set<Speciality> specialities = new HashSet<>(
                specialityRepository.findAllById(dto.getSpecialities()));
        course.setSpecialities(specialities);
        Course saved = courseRepository.save(course);
        return courseMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDto findById(Long id) {
        Course found = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return courseMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDto> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponseDto update(CourseUpdateDto dto) {
        Course found = courseRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Set<Speciality> specialities = new HashSet<>(
                specialityRepository.findAllById(dto.getSpecialities()));
        found.setSpecialities(specialities);
        courseMapper.update(dto, found);
        Course saved = courseRepository.save(found);
        return courseMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course found = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // 1. Delete student course enrollments
        studentCourseRepository.deleteAllByCourseId(id);

        // 2. Delete classes of this course and their class overrides
        List<CourseClass> classes = courseClassRepository.findAllByCourseId(id);
        for (CourseClass cls : classes) {
            classOverrideRepository.deleteAllByCourseClassId(cls.getId());
        }
        courseClassRepository.deleteAll(classes);

        // 3. Clear specialities association (removes rows from course_speciality)
        found.getSpecialities().clear();
        courseRepository.save(found);

        // 4. Delete the course
        courseRepository.delete(found);
    }
}
