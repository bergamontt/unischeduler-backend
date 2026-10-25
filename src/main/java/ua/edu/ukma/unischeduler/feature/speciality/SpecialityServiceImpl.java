package ua.edu.ukma.unischeduler.feature.speciality;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialityServiceImpl implements SpecialityService {
    private final SpecialityRepository specialityRepository;
    private final SpecialityMapper specialityMapper;

    @Override
    public SpecialityResponseDto create(SpecialityCreateDto dto) {
        Speciality speciality = specialityMapper.toEntity(dto);
        Speciality saved = specialityRepository.save(speciality);
        return specialityMapper.toDto(saved);
    }

    @Override
    public SpecialityResponseDto findById(Long id) {
        Speciality found = specialityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Speciality not found"));
        return specialityMapper.toDto(found);
    }

    @Override
    public List<SpecialityResponseDto> findAll() {
        return specialityRepository.findAll()
                .stream()
                .map(specialityMapper::toDto)
                .toList();
    }

    @Override
    public SpecialityResponseDto update(SpecialityUpdateDto dto) {
        Speciality found = specialityRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Speciality not found"));
        specialityMapper.update(dto, found);
        Speciality saved = specialityRepository.save(found);
        return specialityMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!specialityRepository.existsById(id))
            throw new ResourceNotFoundException("Speciality not found");
        specialityRepository.deleteById(id);
    }
}
