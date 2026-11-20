package ua.edu.ukma.unischeduler.feature.personalevent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalEventServiceImpl implements PersonalEventService {
    private final UserRepository userRepository;
    private final PersonalEventRepository personalEventRepository;
    private final PersonalEventMapper personalEventMapper;

    @Override
    @Transactional
    public PersonalEventResponseDto create(PersonalEventCreateDto dto) {
        PersonalEvent personalEvent = personalEventMapper.toEntity(dto);
        if (!userRepository.existsById(dto.getUserId()))
            throw new ResourceNotFoundException("User not found");
        PersonalEvent saved = personalEventRepository.save(personalEvent);
        return personalEventMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalEventResponseDto findById(Long id) {
       PersonalEvent found = personalEventRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Personal event not found"));
       return personalEventMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalEventResponseDto> findAll() {
        return personalEventRepository.findAll()
                .stream()
                .map(personalEventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PersonalEventResponseDto update(PersonalEventUpdateDto dto) {
        PersonalEvent found = personalEventRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Personal event not found"));
        personalEventMapper.update(dto, found);
        PersonalEvent saved = personalEventRepository.save(found);
        return personalEventMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!personalEventRepository.existsById(id))
            throw new ResourceNotFoundException("Personal event not found");
        personalEventRepository.deleteById(id);
    }
}
