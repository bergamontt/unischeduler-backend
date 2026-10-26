package ua.edu.ukma.unischeduler.feature.classoverride;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long user;

    @ManyToOne
    @JoinColumn(name = "course_class_id", nullable = false)
    private CourseClass courseClass;

    @Size(max = 100)
    @Column(name = "teacher_override", length = 100)
    private String teacherOverride;

    @Size(max = 50)
    @Column(name = "group_override", length = 50)
    private String groupOverride;

    @Size(max = 100)
    @Column(name = "class_name_override", length = 100)
    private String classNameOverride;

    @Size(max = 100)
    @Column(name = "location_override", length = 100)
    private String locationOverride;

    @Column(name = "starts_at_override")
    private LocalDateTime startsAtOverride;

    @Column(name = "ends_at_override")
    private LocalDateTime endsAtOverride;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @ColumnDefault("false")
    @Builder.Default
    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;
}
