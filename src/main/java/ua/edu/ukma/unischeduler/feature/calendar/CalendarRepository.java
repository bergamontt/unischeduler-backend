package ua.edu.ukma.unischeduler.feature.calendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarRepository extends JpaRepository<CourseClass, Long> {
    @Query("""
        SELECT cc AS courseClass, co AS override
        FROM CourseClass cc
        LEFT JOIN ClassOverride co
               ON co.courseClass = cc
              AND co.user = :userId
              AND co.isHidden = false
        WHERE (:startsAt IS NULL OR cc.endsAt >= :startsAt)
          AND (:endsAt IS NULL OR cc.startsAt <= :endsAt)
    """)
    List<CourseClassWithOverride> findClassesWithOverrides(
            @Param("userId") Long userId,
            @Param("startsAt") LocalDateTime startsAt,
            @Param("endsAt") LocalDateTime endsAt
    );
}
