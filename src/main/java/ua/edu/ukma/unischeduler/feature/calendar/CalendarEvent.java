package ua.edu.ukma.unischeduler.feature.calendar;

import lombok.*;
import ua.edu.ukma.unischeduler.common.enums.EventType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarEvent {
    private Long eventId;
    private EventType eventType;
    private String name;
    private String teacher;
    private String group;
    private String location;
    private String description;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
