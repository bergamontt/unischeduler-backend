package ua.edu.ukma.unischeduler.feature.speciality;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/speciality")
@RequiredArgsConstructor
public class SpecialityController {
    private final SpecialityService specialityService;

    @PostMapping
    public SpecialityResponseDto create(@RequestBody SpecialityCreateDto dto) {
        return specialityService.create(dto);
    }

    @GetMapping("/{id}")
    public SpecialityResponseDto findById(@PathVariable Long id) {
        return specialityService.findById(id);
    }

    @GetMapping
    public List<SpecialityResponseDto> findAll() {
        return specialityService.findAll();
    }

    @PutMapping
    public SpecialityResponseDto update(@RequestBody SpecialityUpdateDto dto) {
        return specialityService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specialityService.delete(id);
    }
}
