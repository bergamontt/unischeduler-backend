package ua.edu.ukma.unischeduler.feature.courseclass;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.scheduleimport.ImportScope;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_class", uniqueConstraints = @UniqueConstraint(
        name = "uq_import_class_key", columnNames = {"import_scope_id", "source_key"}))
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

    @Size(max = 100)
    @Column(name = "teacher", length = 100)
    private String teacher;

    @Size(max = 50)
    @Column(name = "class_group", length = 50)
    private String group;

    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    // Nullable for user-created classes; import-owned classes are scoped.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_scope_id")
    private ImportScope importScope;

    @Column(name = "source_key", length = 64)
    private String sourceKey;

    @Column(name = "source_row")
    private Integer sourceRow;

    @Column(name = "source_metadata", columnDefinition = "TEXT")
    private String sourceMetadata;

    @Column(name = "description", length = 1000)
    private String description;

    @ColumnDefault("false")
    @Builder.Default
    @Column(name = "archived", nullable = false)
    private Boolean archived = false;
}
