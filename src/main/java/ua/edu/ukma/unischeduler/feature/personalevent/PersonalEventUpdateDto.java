package ua.edu.ukma.unischeduler.feature.personalevent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalEventUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    private String location;

    private String description;

    @NotNull
    private LocalTime startsAt;

    @NotNull
    private LocalTime endsAt;

    @NotNull
    private LocalDate date;
}
