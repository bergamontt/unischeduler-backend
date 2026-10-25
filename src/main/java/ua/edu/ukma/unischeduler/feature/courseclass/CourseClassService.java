package ua.edu.ukma.unischeduler.feature.courseclass;

import java.util.List;

public interface CourseClassService {
    CourseClassResponseDto create(CourseClassCreateDto dto);
    CourseClassResponseDto findById(Long id);
    List<CourseClassResponseDto> findAll();
    CourseClassResponseDto update(CourseClassUpdateDto dto);
    void delete(Long id);
}
