package ua.edu.ukma.unischeduler.feature.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT course_id FROM course_speciality WHERE specialities_id = :specialityId", nativeQuery = true)
    List<Long> findCourseIdsBySpecialityId(@Param("specialityId") Long specialityId);
}
