package ua.edu.ukma.unischeduler.feature.courseclassoverride;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseClassOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "course_class_id")
    private CourseClass courseClass;

    @NotBlank
    @Size(max = 100)
    @Column(name = "teacher_override", length = 100)
    private String teacherOverride;

    @NotBlank
    @Size(max = 50)
    @Column(name = "group_override", length = 50)
    private String groupOverride;

    @NotBlank
    @Size(max = 100)
    @Column(name = "class_name_override", length = 100)
    private String classNameOverride;

    @NotBlank
    @Size(max = 100)
    @Column(name = "auditory_override", length = 100)
    private String auditoryOverride;

    @Column(name = "starts_at_override")
    private LocalTime startsAtOverride;

    @Column(name = "ends_at_override")
    private LocalTime endsAtOverride;

    @Column(name = "date_override")
    private LocalDate dateOverride;

    @NotBlank
    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden;
}
