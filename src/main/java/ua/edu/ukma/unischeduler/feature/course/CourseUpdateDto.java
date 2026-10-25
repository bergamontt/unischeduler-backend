package ua.edu.ukma.unischeduler.feature.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.common.enums.Semester;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String faculty;

    @NotBlank
    @Size(max = 100)
    private String department;

    @NotNull
    private Semester semester;

    @NotNull
    private Integer year;

    @NotNull
    private Set<@NotNull Long> specialities;
}
