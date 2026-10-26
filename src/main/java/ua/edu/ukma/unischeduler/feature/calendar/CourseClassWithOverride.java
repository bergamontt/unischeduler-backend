package ua.edu.ukma.unischeduler.feature.calendar;

import lombok.*;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverride;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseClassWithOverride {
    private CourseClass courseClass;
    private ClassOverride override;
}
