package ua.edu.ukma.unischeduler.feature.courseclass;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class CourseClassController {
    private final CourseClassService courseClassService;

    @PostMapping
    public CourseClassResponseDto create(@RequestBody CourseClassCreateDto dto) {
        return courseClassService.create(dto);
    }

    @GetMapping("/{id}")
    public CourseClassResponseDto findById(@PathVariable Long id) {
        return courseClassService.findById(id);
    }

    @GetMapping
    public List<CourseClassResponseDto> findAll() {
        return courseClassService.findAll();
    }

    @PutMapping
    public CourseClassResponseDto update(@RequestBody CourseClassUpdateDto dto) {
        return courseClassService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseClassService.delete(id);
    }
}
