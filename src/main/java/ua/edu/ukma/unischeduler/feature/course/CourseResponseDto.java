package ua.edu.ukma.unischeduler.feature.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.common.enums.Semester;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityResponseDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String name;
    private String faculty;
    private String department;
    private Semester semester;
    private Integer year;
    private Set<SpecialityResponseDto> specialities;
}
