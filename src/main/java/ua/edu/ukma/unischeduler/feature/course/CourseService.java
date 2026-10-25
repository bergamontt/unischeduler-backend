package ua.edu.ukma.unischeduler.feature.course;

import java.util.List;

public interface CourseService {
    CourseResponseDto create(CourseCreateDto dto);
    CourseResponseDto findById(Long id);
    List<CourseResponseDto> findAll();
    CourseResponseDto update(CourseUpdateDto dto);
    void delete(Long id);
}
