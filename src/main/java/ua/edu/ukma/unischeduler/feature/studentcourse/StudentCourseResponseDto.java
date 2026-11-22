package ua.edu.ukma.unischeduler.feature.studentcourse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.feature.course.CourseResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseResponseDto {
    private Long id;
    private Long userId;
    private Long courseId;
    private CourseResponseDto course;
    private String group;
}
