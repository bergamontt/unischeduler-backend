package ua.edu.ukma.unischeduler.feature.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final SpecialityRepository specialityRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDto create(CourseCreateDto dto) {
        Course course = courseMapper.toEntity(dto);
        Set<Speciality> specialities = new HashSet<>(
                specialityRepository.findAllById(dto.getSpecialities()));
        course.setSpecialities(specialities);
        Course saved = courseRepository.save(course);
        return courseMapper.toDto(saved);
    }

    @Override
    public CourseResponseDto findById(Long id) {
        Course found = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return courseMapper.toDto(found);
    }

    @Override
    public List<CourseResponseDto> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
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
    public void delete(Long id) {
        if (!courseRepository.existsById(id))
            throw new ResourceNotFoundException("Course not found");
        courseRepository.deleteById(id);
    }
}
