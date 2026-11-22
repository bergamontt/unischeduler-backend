package ua.edu.ukma.unischeduler.feature.studentcourse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseCreateDto {
    private Long userId;

    @NotNull
    private Long courseId;

    private String group;
}
