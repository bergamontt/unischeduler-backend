package ua.edu.ukma.unischeduler.feature.classoverride;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class-override")
@RequiredArgsConstructor
public class ClassOverrideController {
    private final ClassOverrideService classOverrideService;

    @PostMapping
    public ClassOverrideResponseDto create(@Valid @RequestBody ClassOverrideCreateDto dto) {
        return classOverrideService.create(dto);
    }

    @GetMapping("/{id}")
    public ClassOverrideResponseDto findById(@PathVariable Long id) {
        return classOverrideService.findById(id);
    }

    @GetMapping
    public List<ClassOverrideResponseDto> findAll() {
        return classOverrideService.findAll();
    }

    @PutMapping
    public ClassOverrideResponseDto update(@Valid @RequestBody ClassOverrideUpdateDto dto) {
        return classOverrideService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        classOverrideService.delete(id);
    }
}
