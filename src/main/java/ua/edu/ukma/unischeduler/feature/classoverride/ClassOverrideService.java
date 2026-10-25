package ua.edu.ukma.unischeduler.feature.classoverride;

import java.util.List;

public interface ClassOverrideService {
    ClassOverrideResponseDto create(ClassOverrideCreateDto dto);
    ClassOverrideResponseDto findById(Long id);
    List<ClassOverrideResponseDto> findAll();
    ClassOverrideResponseDto update(ClassOverrideUpdateDto dto);
    void delete(Long id);
}
