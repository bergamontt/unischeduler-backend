package ua.edu.ukma.unischeduler.feature.speciality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialityUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;
}
