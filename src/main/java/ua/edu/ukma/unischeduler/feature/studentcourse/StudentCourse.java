package ua.edu.ukma.unischeduler.feature.studentcourse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ua.edu.ukma.unischeduler.feature.course.Course;

@Entity
@Table(name = "student_course", uniqueConstraints = {
        @UniqueConstraint(name = "uq_student_course_user_course", columnNames = {"user_id", "course_id"})
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "course_group", length = 50)
    private String group;
}
