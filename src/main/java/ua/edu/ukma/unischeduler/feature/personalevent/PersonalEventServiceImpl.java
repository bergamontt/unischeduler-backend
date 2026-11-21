package ua.edu.ukma.unischeduler.feature.personalevent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalEventServiceImpl implements PersonalEventService {
    private final UserRepository userRepository;
    private final PersonalEventRepository personalEventRepository;
    private final PersonalEventMapper personalEventMapper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public PersonalEventResponseDto create(PersonalEventCreateDto dto) {
        Long userId = currentUserService.resolveUserId(dto.getUserId());
        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found");
        PersonalEvent personalEvent = personalEventMapper.toEntity(dto);
        personalEvent.setUserId(userId);
        PersonalEvent saved = personalEventRepository.save(personalEvent);
        return personalEventMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalEventResponseDto findById(Long id) {
       PersonalEvent found = personalEventRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Personal event not found"));
       currentUserService.verifyOwnership(found.getUserId());
       return personalEventMapper.toDto(found);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalEventResponseDto> findAll() {
        List<PersonalEvent> events = currentUserService.isAdmin()
                ? personalEventRepository.findAll()
                : personalEventRepository.findAllByUserId(currentUserService.getCurrentUser().getId());
        return events.stream()
                .map(personalEventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PersonalEventResponseDto update(PersonalEventUpdateDto dto) {
        PersonalEvent found = personalEventRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Personal event not found"));
        currentUserService.verifyOwnership(found.getUserId());
        personalEventMapper.update(dto, found);
        PersonalEvent saved = personalEventRepository.save(found);
        return personalEventMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PersonalEvent found = personalEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personal event not found"));
        currentUserService.verifyOwnership(found.getUserId());
        personalEventRepository.delete(found);
    }
}
