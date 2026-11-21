package ua.edu.ukma.unischeduler.feature.calendar;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.edu.ukma.unischeduler.common.enums.EventType;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverride;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CalendarEventMapperTest {
    private final CalendarEventMapper mapper = Mappers.getMapper(CalendarEventMapper.class);

    @Test
    void mapsPersonalEvent() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        PersonalEvent personalEvent = PersonalEvent.builder().id(1L).userId(2L).name("Meeting")
                .location("Library").description("Team meeting").startsAt(startsAt).endsAt(startsAt.plusHours(1)).build();

        CalendarEvent result = mapper.toCalendarEvent(personalEvent);

        assertEquals(1L, result.getEventId());
        assertEquals(EventType.PERSONAL, result.getEventType());
        assertEquals("Meeting", result.getName());
        assertEquals("Library", result.getLocation());
        assertEquals("Team meeting", result.getDescription());
        assertEquals(startsAt, result.getStartsAt());
        assertEquals(startsAt.plusHours(1), result.getEndsAt());
        assertNull(result.getTeacher());
        assertNull(result.getGroup());
    }

    @Test
    void mapsCourseClassWithoutOverride() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        CourseClass courseClass = CourseClass.builder().id(1L).course(Course.builder().name("Algorithms").build())
                .teacher("Teacher").group("Group 1").location("Room 101")
                .startsAt(startsAt).endsAt(startsAt.plusHours(1)).build();

        CalendarEvent result = mapper.toCalendarEvent(new CourseClassWithOverride(courseClass, null));

        assertEquals(1L, result.getEventId());
        assertEquals(EventType.COURSE_CLASS, result.getEventType());
        assertEquals("Algorithms", result.getName());
        assertEquals("Teacher", result.getTeacher());
        assertEquals("Group 1", result.getGroup());
        assertEquals("Room 101", result.getLocation());
        assertEquals(startsAt, result.getStartsAt());
        assertEquals(startsAt.plusHours(1), result.getEndsAt());
        assertNull(result.getDescription());
    }

    @Test
    void appliesAllOverriddenFields() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        CourseClass courseClass = CourseClass.builder().id(1L).course(Course.builder().name("Algorithms").build())
                .teacher("Original teacher").group("Group 1").location("Room 101")
                .startsAt(startsAt).endsAt(startsAt.plusHours(1)).build();
        ClassOverride override = ClassOverride.builder().id(2L).classNameOverride("Consultation")
                .teacherOverride("Other teacher").groupOverride("Group 2").locationOverride("Online")
                .startsAtOverride(startsAt.plusHours(2)).endsAtOverride(startsAt.plusHours(3))
                .description("Bring questions").build();

        CalendarEvent result = mapper.toCalendarEvent(new CourseClassWithOverride(courseClass, override));

        assertEquals(2L, result.getEventId());
        assertEquals(EventType.CLASS_OVERRIDE, result.getEventType());
        assertEquals("Consultation", result.getName());
        assertEquals("Other teacher", result.getTeacher());
        assertEquals("Group 2", result.getGroup());
        assertEquals("Online", result.getLocation());
        assertEquals(startsAt.plusHours(2), result.getStartsAt());
        assertEquals(startsAt.plusHours(3), result.getEndsAt());
        assertEquals("Bring questions", result.getDescription());
    }

    @Test
    void keepsOriginalFieldsWhenOverrideValuesAreNull() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        CourseClass courseClass = CourseClass.builder().id(1L).course(Course.builder().name("Algorithms").build())
                .teacher("Teacher").group("Group 1").location("Room 101")
                .startsAt(startsAt).endsAt(startsAt.plusHours(1)).build();
        ClassOverride override = ClassOverride.builder().id(2L).locationOverride("Online").build();

        CalendarEvent result = mapper.toCalendarEvent(new CourseClassWithOverride(courseClass, override));

        assertEquals(2L, result.getEventId());
        assertEquals(EventType.CLASS_OVERRIDE, result.getEventType());
        assertEquals("Online", result.getLocation());
        assertEquals("Algorithms", result.getName());
        assertEquals("Teacher", result.getTeacher());
        assertEquals("Group 1", result.getGroup());
        assertEquals(startsAt, result.getStartsAt());
        assertEquals(startsAt.plusHours(1), result.getEndsAt());
        assertEquals("Room 101", courseClass.getLocation());
    }
}
