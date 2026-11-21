package ua.edu.ukma.unischeduler.feature.classoverride;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassOverrideRepository extends JpaRepository<ClassOverride, Long> {
    List<ClassOverride> findAllByUser(Long user);
}
