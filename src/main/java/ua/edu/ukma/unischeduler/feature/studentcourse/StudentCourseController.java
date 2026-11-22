package ua.edu.ukma.unischeduler.feature.studentcourse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.unischeduler.feature.auth.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/student-courses")
@RequiredArgsConstructor
public class StudentCourseController {
    private final StudentCourseService service;

    private Long resolveUserId(UserPrincipal principal, Long requestedUserId) {
        if (principal != null) {
            return principal.getId();
        }
        return requestedUserId != null ? requestedUserId : 1L;
    }

    @GetMapping
    public List<StudentCourseResponseDto> getCourses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        return service.findByUserId(resolveUserId(principal, userId));
    }

    @PostMapping
    public StudentCourseResponseDto saveCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentCourseCreateDto dto
    ) {
        if (dto.getUserId() == null) {
            dto.setUserId(resolveUserId(principal, null));
        }
        return service.save(dto);
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long courseId,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        service.delete(resolveUserId(principal, userId), courseId);
        return ResponseEntity.noContent().build();
    }
}
