package ua.edu.ukma.unischeduler.feature.courseclass;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseClassUpdateDto {
    @NotNull
    private Long id;

    @NotNull
    private Long course;

    @NotBlank
    @Size(max = 100)
    private String teacher;

    @NotBlank
    @Size(max = 50)
    private String group;

    @NotBlank
    @Size(max = 100)
    private String location;

    @NotNull
    private LocalDateTime startsAt;

    @NotNull
    private LocalDateTime endsAt;
}
