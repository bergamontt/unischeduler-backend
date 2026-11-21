package ua.edu.ukma.unischeduler.feature.personalevent;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.unischeduler.feature.calendar.CalendarEventFilterDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalEventSpecificationTest {
    @Mock
    private Root<PersonalEvent> root;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Long> userIdPath;

    @Mock
    private Path<LocalDateTime> timePath;

    @Mock
    private Predicate predicate;

    @Test
    void hasUserIdMatchesRequestedUser() {
        when(root.<Long>get("userId")).thenReturn(userIdPath);
        when(criteriaBuilder.equal(userIdPath, 1L)).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.hasUserId(1L).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).equal(userIdPath, 1L);
    }

    @Test
    void hasUserIdReturnsDisjunctionWhenUserIdIsNull() {
        when(criteriaBuilder.disjunction()).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.hasUserId(null).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).disjunction();
        verifyNoInteractions(root);
    }

    @Test
    void endsAfterOrEqualToIncludesEventsEndingAtRangeStart() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        when(root.<LocalDateTime>get("endsAt")).thenReturn(timePath);
        when(criteriaBuilder.greaterThanOrEqualTo(timePath, startsAt)).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.endsAfterOrEqualTo(startsAt).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).greaterThanOrEqualTo(timePath, startsAt);
    }

    @Test
    void endsAfterOrEqualToDoesNotFilterWhenStartIsNull() {
        assertNull(PersonalEventSpecification.endsAfterOrEqualTo(null).toPredicate(root, null, criteriaBuilder));

        verifyNoInteractions(root, criteriaBuilder);
    }

    @Test
    void startsBeforeOrEqualToIncludesEventsStartingAtRangeEnd() {
        LocalDateTime endsAt = LocalDateTime.of(2026, 11, 20, 18, 0);
        when(root.<LocalDateTime>get("startsAt")).thenReturn(timePath);
        when(criteriaBuilder.lessThanOrEqualTo(timePath, endsAt)).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.startsBeforeOrEqualTo(endsAt).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).lessThanOrEqualTo(timePath, endsAt);
    }

    @Test
    void startsBeforeOrEqualToDoesNotFilterWhenEndIsNull() {
        assertNull(PersonalEventSpecification.startsBeforeOrEqualTo(null).toPredicate(root, null, criteriaBuilder));

        verifyNoInteractions(root, criteriaBuilder);
    }

    @Test
    void buildCombinesUserAndTimeFilters() {
        LocalDateTime startsAt = LocalDateTime.of(2026, 11, 20, 8, 0);
        LocalDateTime endsAt = startsAt.plusHours(10);
        CalendarEventFilterDto filter = new CalendarEventFilterDto(1L, startsAt, endsAt);
        when(root.<Long>get("userId")).thenReturn(userIdPath);
        when(root.<LocalDateTime>get("endsAt")).thenReturn(timePath);
        when(root.<LocalDateTime>get("startsAt")).thenReturn(timePath);
        when(criteriaBuilder.equal(userIdPath, 1L)).thenReturn(predicate);
        when(criteriaBuilder.greaterThanOrEqualTo(timePath, startsAt)).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(timePath, endsAt)).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.build(filter).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).equal(userIdPath, 1L);
        verify(criteriaBuilder).greaterThanOrEqualTo(timePath, startsAt);
        verify(criteriaBuilder).lessThanOrEqualTo(timePath, endsAt);
        verify(criteriaBuilder, times(2)).and(predicate, predicate);
    }

    @Test
    void buildReturnsDisjunctionWhenUserIdIsNull() {
        when(criteriaBuilder.disjunction()).thenReturn(predicate);

        Predicate result = PersonalEventSpecification.build(new CalendarEventFilterDto()).toPredicate(root, null, criteriaBuilder);

        assertSame(predicate, result);
        verify(criteriaBuilder).disjunction();
        verifyNoInteractions(root);
    }
}
