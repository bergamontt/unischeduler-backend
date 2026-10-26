package ua.edu.ukma.unischeduler.feature.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEvent;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEventRepository;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEventSpecification;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    private final PersonalEventRepository personalEventRepository;
    private final CalendarEventMapper calendarEventMapper;

    public List<CalendarEvent> findAll(CalendarEventFilterDto filter) {
        List<CalendarEvent> classEvents = calendarRepository.findClassesWithOverrides(
                        filter.getUserId(),
                        filter.getStartsAt(),
                        filter.getEndsAt()
                )
                .stream()
                .map(calendarEventMapper::toCalendarEvent)
                .toList();
        Specification<PersonalEvent> personalSpecification = PersonalEventSpecification.build(filter);
        List<CalendarEvent> personalEvents = personalEventRepository.findAll(personalSpecification)
                .stream()
                .map(calendarEventMapper::toCalendarEvent)
                .toList();
        return Stream.concat(classEvents.stream(), personalEvents.stream())
                .sorted(Comparator.comparing(CalendarEvent::getStartsAt))
                .toList();
    }
}
