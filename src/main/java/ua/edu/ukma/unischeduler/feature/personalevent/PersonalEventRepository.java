package ua.edu.ukma.unischeduler.feature.personalevent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonalEventRepository extends JpaRepository<PersonalEvent, Long>, JpaSpecificationExecutor<PersonalEvent> {
}
