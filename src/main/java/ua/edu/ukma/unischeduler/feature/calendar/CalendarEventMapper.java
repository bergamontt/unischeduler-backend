package ua.edu.ukma.unischeduler.feature.calendar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverride;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.personalevent.PersonalEvent;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarEventMapper {

    @Mapping(target = "eventId", source = "id")
    @Mapping(target = "eventType", constant = "PERSONAL")
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "group", ignore = true)
    CalendarEvent toCalendarEvent(PersonalEvent personalEvent);

    @Mapping(target = "eventId", source = "id")
    @Mapping(target = "eventType", constant = "COURSE_CLASS")
    @Mapping(target = "name", source = "course.name")
    @Mapping(target = "description", ignore = true)
    CalendarEvent toCalendarEvent(CourseClass courseClass);

    @Mapping(target = "eventId", source = "id")
    @Mapping(target = "eventType", constant = "CLASS_OVERRIDE")
    @Mapping(target = "name", source = "classNameOverride")
    @Mapping(target = "teacher", source = "teacherOverride")
    @Mapping(target = "group", source = "groupOverride")
    @Mapping(target = "location", source = "locationOverride")
    @Mapping(target = "startsAt", source = "startsAtOverride")
    @Mapping(target = "endsAt", source = "endsAtOverride")
    @Mapping(target = "description", source = "description")
    void apply(@MappingTarget CalendarEvent target, ClassOverride override);

    default CalendarEvent toCalendarEvent(CourseClassWithOverride projection) {
        CalendarEvent event = toCalendarEvent(projection.getCourseClass());
        if (projection.getOverride() != null)
            apply(event, projection.getOverride());
        return event;
    }
}
