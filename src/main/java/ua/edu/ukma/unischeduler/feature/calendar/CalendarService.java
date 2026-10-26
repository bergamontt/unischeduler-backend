package ua.edu.ukma.unischeduler.feature.calendar;

import java.util.List;

public interface CalendarService {
    List<CalendarEvent> findAll(CalendarEventFilterDto filter);
}
