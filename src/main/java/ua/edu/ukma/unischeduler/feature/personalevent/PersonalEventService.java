package ua.edu.ukma.unischeduler.feature.personalevent;

import java.util.List;

public interface PersonalEventService {
    PersonalEventResponseDto create(PersonalEventCreateDto dto);
    PersonalEventResponseDto findById(Long id);
    List<PersonalEventResponseDto> findAll();
    PersonalEventResponseDto update(PersonalEventUpdateDto dto);
    void delete(Long id);
}
