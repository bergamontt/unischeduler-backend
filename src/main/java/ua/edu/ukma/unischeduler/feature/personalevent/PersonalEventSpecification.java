package ua.edu.ukma.unischeduler.feature.personalevent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ua.edu.ukma.unischeduler.feature.calendar.CalendarEventFilterDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalEventSpecification {

    public static Specification<PersonalEvent> hasUserId(Long userId) {
        return (root, _, criteriaBuilder) -> {
            if (userId == null)
                return null;
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    public static Specification<PersonalEvent> endsAfterOrEqualTo(LocalDateTime startsAt) {
        return (root, _, criteriaBuilder) -> {
            if (startsAt == null)
                return null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get("endsAt"), startsAt);
        };
    }

    public static Specification<PersonalEvent> startsBeforeOrEqualTo(LocalDateTime endsAt) {
        return (root, _, criteriaBuilder) -> {
            if (endsAt == null)
                return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get("startsAt"), endsAt);
        };
    }

    public static Specification<PersonalEvent> build(CalendarEventFilterDto filters) {
        return Specification.allOf(
                hasUserId(filters.getUserId()),
                endsAfterOrEqualTo(filters.getStartsAt()),
                startsBeforeOrEqualTo(filters.getEndsAt())
        );
    }
}
