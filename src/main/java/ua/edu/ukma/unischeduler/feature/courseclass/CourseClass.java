package ua.edu.ukma.unischeduler.feature.courseclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.unischeduler.feature.course.Course;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank
    @Size(max = 100)
    @Column(name = "teacher", length = 100)
    private String teacher;

    @NotBlank
    @Size(max = 50)
    @Column(name = "class_group", length = 50)
    private String group;

    @NotBlank
    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;
}
