package ua.edu.ukma.unischeduler.feature.classoverride;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassResponseDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassOverrideResponseDto {
    private Long id;
    private Long user;
    private CourseClassResponseDto courseClass;
    private String teacherOverride;
    private String groupOverride;
    private String classNameOverride;
    private String locationOverride;
    private LocalDateTime startsAtOverride;
    private LocalDateTime endsAtOverride;
    private String description;
    private Boolean isHidden;
}
