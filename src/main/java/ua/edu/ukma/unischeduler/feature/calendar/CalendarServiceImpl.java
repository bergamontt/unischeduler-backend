package ua.edu.ukma.unischeduler.feature.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
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
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEvent> findAll(CalendarEventFilterDto filter) {
        if (filter.getUserId() != null) {
            currentUserService.verifyOwnership(filter.getUserId());
        }
        List<CalendarEvent> classEvents = calendarRepository.findClassesWithOverrides(
                        filter.getUserId(),
                        filter.getStartsAt(),
                        filter.getEndsAt()
                )
                .stream()
                .map(calendarEventMapper::toCalendarEvent)
                .toList();
        List<CalendarEvent> personalEvents = filter.getUserId() == null
                ? List.of()
                : personalEventRepository.findAll(PersonalEventSpecification.build(filter))
                        .stream()
                        .map(calendarEventMapper::toCalendarEvent)
                        .toList();
        return Stream.concat(classEvents.stream(), personalEvents.stream())
                .sorted(Comparator.comparing(CalendarEvent::getStartsAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }
}
