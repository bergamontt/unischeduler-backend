package ua.edu.ukma.unischeduler.feature.speciality;

import java.util.List;

public interface SpecialityService {
    SpecialityResponseDto create(SpecialityCreateDto dto);
    SpecialityResponseDto findById(Long id);
    List<SpecialityResponseDto> findAll();
    SpecialityResponseDto update(SpecialityUpdateDto dto);
    void delete(Long id);
}
