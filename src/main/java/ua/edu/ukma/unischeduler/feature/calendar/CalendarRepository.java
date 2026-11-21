package ua.edu.ukma.unischeduler.feature.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarRepository extends JpaRepository<CourseClass, Long> {
    @Query("""
        SELECT new ua.edu.ukma.unischeduler.feature.calendar.CourseClassWithOverride(cc, co)
        FROM CourseClass cc
        LEFT JOIN ClassOverride co
               ON co.courseClass = cc
              AND cast(:userId as Long) IS NOT NULL
              AND co.user = :userId
        WHERE cc.archived = false
          AND (co IS NULL OR co.isHidden = false)
          AND (cast(:startsAt as timestamp) IS NULL OR COALESCE(co.endsAtOverride, cc.endsAt) >= :startsAt)
          AND (cast(:endsAt as timestamp) IS NULL OR COALESCE(co.startsAtOverride, cc.startsAt) <= :endsAt)
    """)
    List<CourseClassWithOverride> findClassesWithOverrides(
            @Param("userId") Long userId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt
    );
}
