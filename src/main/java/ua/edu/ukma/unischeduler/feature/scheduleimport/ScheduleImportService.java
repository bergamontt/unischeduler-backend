package ua.edu.ukma.unischeduler.feature.scheduleimport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import ua.edu.ukma.unischeduler.feature.speciality.*;
import ua.edu.ukma.unischeduler.feature.course.*;
import ua.edu.ukma.unischeduler.feature.courseclass.*;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverrideRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleImportService {
    private final SpecialityRepository specialityRepository;
    private final CourseRepository courseRepository;
    private final CourseClassRepository classRepository;
    private final ImportScopeRepository scopeRepository;
    private final ClassOverrideRepository overrideRepository;

    private String normal(String s) {
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    @Transactional
    public ScheduleImportResult replace(ScheduleImportRequest request) {
        // Reject a malformed request BEFORE any writes. @Valid handles lengths/nullability.
        int year = Integer.parseInt(request.academicYear().substring(0, 4));
        Set<String> inputKeys = new HashSet<>();
        for (ScheduleImportOccurrenceDto dto : request.occurrences()) {
            if (!dto.startsAt().isBefore(dto.endsAt()) || dto.startsAt().getYear() != year ||
                    dto.endsAt().getYear() != year ||
                    !dto.startsAt().toLocalDate().equals(dto.endsAt().toLocalDate())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid occurrence dates for import sourceKey=" + dto.sourceKey());
            }
            if (!inputKeys.add(dto.sourceKey())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate sourceKey in import payload");
            }
        }

        Speciality speciality = specialityRepository.findAll().stream()
                .filter(s -> normal(s.getName()).equals(normal(request.specialityName())))
                .findFirst().orElse(null);
        int specialitiesCreated = 0;
        if (speciality == null) {
            speciality = specialityRepository.save(Speciality.builder().name(request.specialityName()).build());
            specialitiesCreated++;
        }
        final Speciality scopeSpeciality = speciality;
        ImportScope scope = scopeRepository.findBySpeciality_IdAndStudyYearAndSemesterAndAcademicYear(
                speciality.getId(), request.studyYear(), request.semester(), request.academicYear())
                .orElseGet(() -> scopeRepository.save(ImportScope.builder()
                        .speciality(scopeSpeciality).studyYear(request.studyYear())
                        .semester(request.semester()).academicYear(request.academicYear()).build()));

        Map<String, Course> courses = new HashMap<>();
        for (Course c : courseRepository.findAll()) {
            if (Objects.equals(c.getYear(), request.studyYear()) &&
                    c.getSemester() == request.semester() &&
                    normal(c.getFaculty()).equals(normal(request.faculty())) &&
                    normal(c.getDepartment()).equals(normal(request.department())) &&
                    c.getSpecialities().stream().anyMatch(s -> s.getId().equals(scopeSpeciality.getId()))) {
                courses.putIfAbsent(normal(c.getName()), c);
            }
        }

        Map<String, CourseClass> old = new HashMap<>();
        for (CourseClass cc : classRepository.findAllByImportScope_Id(scope.getId())) {
            old.put(cc.getSourceKey(), cc);
        }
        int coursesCreated=0, created=0, updated=0, unchanged=0, archived=0, deleted=0;
        for (ScheduleImportOccurrenceDto dto : request.occurrences()) {
            String courseKey = normal(dto.courseName());
            Course course = courses.get(courseKey);
            if (course == null) {
                course = Course.builder()
                        .name(dto.courseName()).faculty(request.faculty())
                        .department(request.department()).semester(request.semester())
                        .year(request.studyYear()).specialities(new HashSet<>(Set.of(speciality))).build();
                course = courseRepository.save(course);
                courses.put(courseKey, course);
                coursesCreated++;
            }
            CourseClass cc = old.remove(dto.sourceKey());
            if (cc == null) {
                cc = new CourseClass();
                cc.setSourceKey(dto.sourceKey());
                cc.setImportScope(scope);
                created++;
            } else {
                boolean same = Objects.equals(cc.getCourse().getId(), course.getId()) &&
                        Objects.equals(cc.getTeacher(), dto.teacher()) &&
                        Objects.equals(cc.getGroup(), dto.group()) &&
                        Objects.equals(cc.getLocation(), dto.location()) &&
                        Objects.equals(cc.getStartsAt(), dto.startsAt()) &&
                        Objects.equals(cc.getEndsAt(), dto.endsAt()) &&
                        Objects.equals(cc.getDescription(), dto.description()) &&
                        Objects.equals(cc.getSourceRow(), dto.sourceRow()) &&
                        Objects.equals(cc.getSourceMetadata(), dto.sourceMetadata()) &&
                        !Boolean.TRUE.equals(cc.getArchived());
                if (same) {
                    unchanged++;
                    continue;
                }
                updated++;
            }
            cc.setCourse(course);
            cc.setTeacher(dto.teacher());
            cc.setGroup(dto.group());
            cc.setLocation(dto.location());
            cc.setStartsAt(dto.startsAt());
            cc.setEndsAt(dto.endsAt());
            cc.setDescription(dto.description());
            cc.setSourceRow(dto.sourceRow());
            cc.setSourceMetadata(dto.sourceMetadata());
            cc.setArchived(false);
            classRepository.save(cc);
        }
        for (CourseClass stale : old.values()) {
            if (Boolean.TRUE.equals(stale.getArchived())) continue;
            if (overrideRepository.existsByCourseClass_Id(stale.getId())) {
                stale.setArchived(true);
                classRepository.save(stale);
                archived++;
            } else {
                classRepository.delete(stale);
                deleted++;
            }
        }
        return new ScheduleImportResult(scope.getId(), specialitiesCreated, coursesCreated,
                created, updated, unchanged, archived, deleted);
    }
}
