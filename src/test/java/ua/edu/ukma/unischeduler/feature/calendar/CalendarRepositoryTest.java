package ua.edu.ukma.unischeduler.feature.calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import ua.edu.ukma.unischeduler.common.enums.Semester;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverride;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverrideRepository;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CalendarRepositoryTest {

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseClassRepository courseClassRepository;

    @Autowired
    private ClassOverrideRepository classOverrideRepository;

    private CourseClass classA;
    private CourseClass classB;

    @BeforeEach
    void setUp() {
        Course course = courseRepository.save(Course.builder()
                .name("Software Design")
                .faculty("FI")
                .department("Computer Science")
                .semester(Semester.AUTUMN)
                .year(2026)
                .build());

        classA = courseClassRepository.save(CourseClass.builder()
                .course(course)
                .teacher("Prof. A")
                .group("SE-1")
                .location("1-201")
                .startsAt(LocalDateTime.of(2026, 11, 21, 10, 0))
                .endsAt(LocalDateTime.of(2026, 11, 21, 11, 20))
                .build());

        classB = courseClassRepository.save(CourseClass.builder()
                .course(course)
                .teacher("Prof. B")
                .group("SE-2")
                .location("1-305")
                .startsAt(LocalDateTime.of(2026, 11, 21, 14, 0))
                .endsAt(LocalDateTime.of(2026, 11, 21, 15, 20))
                .build());

        classOverrideRepository.save(ClassOverride.builder()
                .user(1L)
                .courseClass(classA)
                .isHidden(true)
                .build());

        classOverrideRepository.save(ClassOverride.builder()
                .user(1L)
                .courseClass(classB)
                .locationOverride("Online")
                .isHidden(false)
                .build());
    }

    @Test
    void findClassesWithOverridesExcludesHiddenClassesForUser() {
        List<CourseClassWithOverride> result = calendarRepository.findClassesWithOverrides(1L, null, null);

        assertEquals(1, result.size());
        assertEquals(classB.getId(), result.getFirst().getCourseClass().getId());
        assertNotNull(result.getFirst().getOverride());
        assertEquals("Online", result.getFirst().getOverride().getLocationOverride());
    }

    @Test
    void findClassesWithOverridesDoesNotLeakOverridesWhenUserIdIsNull() {
        List<CourseClassWithOverride> result = calendarRepository.findClassesWithOverrides(null, null, null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getOverride() == null));
    }

    @Test
    void findClassesWithOverridesDoesNotApplyOtherUsersOverrides() {
        List<CourseClassWithOverride> result = calendarRepository.findClassesWithOverrides(2L, null, null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getOverride() == null));
    }

    @Test
    void findClassesWithOverridesFiltersByDateRange() {
        LocalDateTime from = LocalDateTime.of(2026, 11, 21, 11, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 21, 13, 0);

        List<CourseClassWithOverride> result = calendarRepository.findClassesWithOverrides(null, from, to);

        assertEquals(1, result.size());
        assertEquals(classA.getId(), result.getFirst().getCourseClass().getId());
    }
}
