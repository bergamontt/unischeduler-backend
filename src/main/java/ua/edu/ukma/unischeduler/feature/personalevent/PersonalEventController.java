package ua.edu.ukma.unischeduler.feature.personalevent;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personal-event")
@RequiredArgsConstructor
public class PersonalEventController {
    private final PersonalEventService personalEventService;

    @PostMapping
    public PersonalEventResponseDto create(@RequestBody PersonalEventCreateDto dto) {
        return personalEventService.create(dto);
    }

    @GetMapping("/{id}")
    public PersonalEventResponseDto findById(@PathVariable Long id) {
        return personalEventService.findById(id);
    }

    @GetMapping
    public List<PersonalEventResponseDto> findAll() {
        return personalEventService.findAll();
    }

    @PutMapping
    public PersonalEventResponseDto update(@RequestBody PersonalEventUpdateDto dto) {
        return personalEventService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        personalEventService.delete(id);
    }
}
