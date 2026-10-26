package ua.edu.ukma.unischeduler.feature.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping()
    public List<CalendarEvent> findAll(@ModelAttribute CalendarEventFilterDto filter) {
        return calendarService.findAll(filter);
    }
}
