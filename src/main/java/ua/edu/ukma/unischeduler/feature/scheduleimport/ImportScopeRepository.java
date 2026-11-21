package ua.edu.ukma.unischeduler.feature.scheduleimport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import ua.edu.ukma.unischeduler.common.enums.Semester;
import java.util.Optional;

public interface ImportScopeRepository extends JpaRepository<ImportScope, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ImportScope> findBySpeciality_IdAndStudyYearAndSemesterAndAcademicYear(
            Long specialityId, Integer studyYear, Semester semester, String academicYear);
}
