package ua.edu.ukma.unischeduler.feature.speciality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM course_speciality WHERE specialities_id = :id", nativeQuery = true)
    void deleteCourseSpecialityLinks(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE \"user\" SET speciality_id = NULL WHERE speciality_id = :id", nativeQuery = true)
    void unlinkUsers(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE course_class SET import_scope_id = NULL WHERE import_scope_id IN (SELECT id FROM schedule_import_scope WHERE speciality_id = :id)", nativeQuery = true)
    void unlinkImportScopesFromClasses(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM schedule_import_scope WHERE speciality_id = :id", nativeQuery = true)
    void deleteImportScopes(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM speciality WHERE id = :id", nativeQuery = true)
    void deleteSpecialityByIdNative(@Param("id") Long id);
}
