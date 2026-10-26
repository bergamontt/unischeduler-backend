package ua.edu.ukma.unischeduler.feature.course;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public CourseResponseDto create(@RequestBody CourseCreateDto dto) {
        return courseService.create(dto);
    }

    @GetMapping("/{id}")
    public CourseResponseDto findById(@PathVariable Long id) {
        return courseService.findById(id);
    }

    @GetMapping
    public List<CourseResponseDto> findAll() {
        return courseService.findAll();
    }

    @PutMapping
    public CourseResponseDto update(@RequestBody CourseUpdateDto dto) {
        return courseService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
