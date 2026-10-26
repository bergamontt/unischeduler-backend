package ua.edu.ukma.unischeduler.feature.classoverride;

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
public class ClassOverrideUpdateDto {
    @NotNull
    private Long id;

    @Size(max = 100)
    private String teacherOverride;

    @Size(max = 50)
    private String groupOverride;

    @Size(max = 100)
    private String classNameOverride;

    @Size(max = 100)
    private String locationOverride;

    private LocalDateTime startsAtOverride;

    private LocalDateTime endsAtOverride;

    @Size(max = 200)
    private String description;

    @NotNull
    private Boolean isHidden;
}
