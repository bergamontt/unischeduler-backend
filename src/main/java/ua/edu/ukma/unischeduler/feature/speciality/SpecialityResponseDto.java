package ua.edu.ukma.unischeduler.feature.speciality;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecialityResponseDto {
    private Long id;
    private String name;
}
