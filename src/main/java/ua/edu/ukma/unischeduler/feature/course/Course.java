package ua.edu.ukma.unischeduler.feature.course;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.unischeduler.common.Semester;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(name = "faculty", nullable = false, length = 100)
    private String faculty;

    @NotBlank
    @Size(max = 100)
    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Semester semester;

    @Positive
    @Column(name = "year", nullable = false)
    private Integer year;

    @ManyToMany
    @JoinTable(name = "course_speciality")
    @Builder.Default
    private Set<@NotNull Speciality> specialities = new HashSet<>();
}
