package ua.edu.ukma.unischeduler.feature.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.ukma.unischeduler.common.enums.EventType;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverride;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEvent;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private PersonalEventRepository personalEventRepository;

    private CalendarServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CalendarServiceImpl(calendarRepository, personalEventRepository, Mappers.getMapper(CalendarEventMapper.class));
    }

    @Test
    void findAllCombinesAndSortsClassesAndPersonalEvents() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        LocalDateTime endsAt = startsAt.plusHours(10);
        CalendarEventFilterDto filter = new CalendarEventFilterDto(1L, startsAt, endsAt);
        CourseClass courseClass = CourseClass.builder().id(2L).startsAt(startsAt.plusHours(2)).build();
        PersonalEvent earlier = PersonalEvent.builder().id(3L).startsAt(startsAt.plusHours(1)).build();
        PersonalEvent later = PersonalEvent.builder().id(4L).startsAt(startsAt.plusHours(3)).build();
        when(calendarRepository.findClassesWithOverrides(1L, startsAt, endsAt))
                .thenReturn(List.of(new CourseClassWithOverride(courseClass, null)));
        when(personalEventRepository.findAll(ArgumentMatchers.<Specification<PersonalEvent>>any())).thenReturn(List.of(later, earlier));

        List<CalendarEvent> result = service.findAll(filter);

        assertEquals(List.of(3L, 2L, 4L), result.stream().map(CalendarEvent::getEventId).toList());
        assertEquals(List.of(EventType.PERSONAL, EventType.COURSE_CLASS, EventType.PERSONAL),
                result.stream().map(CalendarEvent::getEventType).toList());
        verify(calendarRepository).findClassesWithOverrides(1L, startsAt, endsAt);
    }

    @Test
    void findAllSortsClassesByOverriddenStartTime() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        CourseClass courseClass = CourseClass.builder().id(2L)
                .course(Course.builder().name("Algorithms").build()).startsAt(startsAt).build();
        ClassOverride override = ClassOverride.builder().id(5L).classNameOverride("Consultation")
                .startsAtOverride(startsAt.plusHours(3)).build();
        PersonalEvent personalEvent = PersonalEvent.builder().id(3L).startsAt(startsAt.plusHours(1)).build();
        when(calendarRepository.findClassesWithOverrides(null, null, null))
                .thenReturn(List.of(new CourseClassWithOverride(courseClass, override)));
        when(personalEventRepository.findAll(ArgumentMatchers.<Specification<PersonalEvent>>any())).thenReturn(List.of(personalEvent));

        List<CalendarEvent> result = service.findAll(new CalendarEventFilterDto());

        assertEquals(List.of(3L, 5L), result.stream().map(CalendarEvent::getEventId).toList());
        assertEquals("Consultation", result.get(1).getName());
        assertEquals(EventType.CLASS_OVERRIDE, result.get(1).getEventType());
        assertEquals(startsAt.plusHours(3), result.get(1).getStartsAt());
    }

    @Test
    void findAllPlacesEventsWithoutStartTimeLast() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        CourseClass courseClass = CourseClass.builder().id(2L).build();
        PersonalEvent personalEvent = PersonalEvent.builder().id(3L).startsAt(startsAt).build();
        when(calendarRepository.findClassesWithOverrides(null, null, null))
                .thenReturn(List.of(new CourseClassWithOverride(courseClass, null)));
        when(personalEventRepository.findAll(ArgumentMatchers.<Specification<PersonalEvent>>any())).thenReturn(List.of(personalEvent));

        List<CalendarEvent> result = service.findAll(new CalendarEventFilterDto());

        assertEquals(List.of(3L, 2L), result.stream().map(CalendarEvent::getEventId).toList());
        assertNull(result.get(1).getStartsAt());
    }

    @Test
    void findAllReturnsPersonalEventsWhenThereAreNoClasses() {
        PersonalEvent personalEvent = PersonalEvent.builder().id(3L).name("Meeting").build();
        when(calendarRepository.findClassesWithOverrides(null, null, null)).thenReturn(List.of());
        when(personalEventRepository.findAll(ArgumentMatchers.<Specification<PersonalEvent>>any())).thenReturn(List.of(personalEvent));

        List<CalendarEvent> result = service.findAll(new CalendarEventFilterDto());

        assertEquals(1, result.size());
        assertEquals("Meeting", result.getFirst().getName());
        assertEquals(EventType.PERSONAL, result.getFirst().getEventType());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoEvents() {
        when(calendarRepository.findClassesWithOverrides(null, null, null)).thenReturn(List.of());
        when(personalEventRepository.findAll(ArgumentMatchers.<Specification<PersonalEvent>>any())).thenReturn(List.of());

        assertTrue(service.findAll(new CalendarEventFilterDto()).isEmpty());
    }
}
