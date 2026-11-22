package ua.edu.ukma.unischeduler.feature.studentcourse;

import java.util.List;

public interface StudentCourseService {
    List<StudentCourseResponseDto> findByUserId(Long userId);
    StudentCourseResponseDto save(StudentCourseCreateDto dto);
    void delete(Long userId, Long courseId);
}
