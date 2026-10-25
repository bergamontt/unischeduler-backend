package ua.edu.ukma.unischeduler.feature.personalevent;

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
public class PersonalEventResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private String location;
    private String description;
    private LocalTime startsAt;
    private LocalTime endsAt;
    private LocalDate date;
}
