package ua.edu.ukma.unischeduler.feature.scheduleimport;

import jakarta.persistence.*;
import lombok.*;
import ua.edu.ukma.unischeduler.common.enums.Semester;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;

@Entity
@Table(name = "schedule_import_scope", uniqueConstraints = @UniqueConstraint(
        name = "uq_schedule_import_scope",
        columnNames = {"speciality_id", "study_year", "semester", "academic_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportScope {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "speciality_id", nullable = false)
    private Speciality speciality;

    @Column(name = "study_year", nullable = false)
    private Integer studyYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Semester semester;

    @Column(name = "academic_year", length = 9, nullable = false)
    private String academicYear;
}
