package ua.edu.ukma.unischeduler.feature.academicperiod;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.unischeduler.common.enums.Semester;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "academic_period")
public class AcademicPeriod {
    public static final int SINGLETON_ID = 1;

    @Id
    @Builder.Default
    private Integer id = SINGLETON_ID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semester;

    @Column(nullable = false)
    private LocalDate startDate;
}
