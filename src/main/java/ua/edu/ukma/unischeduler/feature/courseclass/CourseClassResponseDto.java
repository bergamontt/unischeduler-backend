package ua.edu.ukma.unischeduler.feature.courseclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.feature.course.Course;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseClassResponseDto {
    private Long id;
    private Course course;
    private String teacher;
    private String group;
    private String location;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
