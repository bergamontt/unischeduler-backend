package ua.edu.ukma.unischeduler.feature.studentcourse;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    @EntityGraph(attributePaths = {"course", "course.specialities"})
    List<StudentCourse> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"course", "course.specialities"})
    Optional<StudentCourse> findByUserIdAndCourseId(Long userId, Long courseId);

    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    void deleteAllByCourseId(Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
