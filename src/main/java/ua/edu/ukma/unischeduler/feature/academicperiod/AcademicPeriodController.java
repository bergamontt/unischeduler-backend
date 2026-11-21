package ua.edu.ukma.unischeduler.feature.academicperiod;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/academic-period")
public class AcademicPeriodController {
    private final AcademicPeriodService service;

    @GetMapping
    public AcademicPeriodResponseDto getCurrent() {
        return service.getCurrent();
    }

    @PutMapping
    public AcademicPeriodResponseDto update(
            @Valid @RequestBody AcademicPeriodUpdateDto request
    ) {
        return service.update(request);
    }
}
