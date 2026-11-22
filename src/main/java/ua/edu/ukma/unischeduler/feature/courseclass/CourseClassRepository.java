package ua.edu.ukma.unischeduler.feature.courseclass;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseClassRepository extends JpaRepository<CourseClass, Long> {
    List<CourseClass> findAllByImportScope_Id(Long importScopeId);
}
