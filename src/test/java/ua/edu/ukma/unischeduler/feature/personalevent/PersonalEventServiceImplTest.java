package ua.edu.ukma.unischeduler.feature.personalevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
import ua.edu.ukma.unischeduler.feature.auth.UserPrincipal;
import ua.edu.ukma.unischeduler.feature.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalEventServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonalEventRepository personalEventRepository;

    @Mock
    private CurrentUserService currentUserService;

    private PersonalEventServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(currentUserService.isAdmin()).thenReturn(true);
        lenient().when(currentUserService.resolveUserId(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new PersonalEventServiceImpl(userRepository, personalEventRepository, Mappers.getMapper(PersonalEventMapper.class), currentUserService);
    }

    @Test
    void createSavesPersonalEvent() {
        PersonalEventCreateDto dto = PersonalEventCreateDto.builder().userId(2L).name("Meeting").build();
        when(userRepository.existsById(2L)).thenReturn(true);
        when(personalEventRepository.save(any(PersonalEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PersonalEventResponseDto result = service.create(dto);

        assertEquals("Meeting", result.getName());
        assertEquals(2L, result.getUserId());
        verify(personalEventRepository).save(any(PersonalEvent.class));
    }

    @Test
    void findByIdReturnsPersonalEvent() {
        PersonalEvent existing = PersonalEvent.builder().id(1L).name("Meeting").build();
        when(personalEventRepository.findById(1L)).thenReturn(Optional.of(existing));

        PersonalEventResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Meeting", result.getName());
    }

    @Test
    void findByIdThrowsWhenPersonalEventDoesNotExist() {
        when(personalEventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Personal event not found", exception.getMessage());
    }

    @Test
    void findAllReturnsPersonalEvents() {
        PersonalEvent first = PersonalEvent.builder().id(1L).name("Meeting").build();
        PersonalEvent second = PersonalEvent.builder().id(2L).name("Consultation").build();
        when(personalEventRepository.findAll()).thenReturn(List.of(first, second));

        List<PersonalEventResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(PersonalEventResponseDto::getId).toList());
        assertEquals(List.of("Meeting", "Consultation"), result.stream().map(PersonalEventResponseDto::getName).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoPersonalEvents() {
        when(personalEventRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        PersonalEventUpdateDto dto = PersonalEventUpdateDto.builder().id(1L).name("Consultation").build();
        PersonalEvent existing = PersonalEvent.builder().id(1L).userId(2L).name("Meeting").build();
        when(personalEventRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(personalEventRepository.save(existing)).thenReturn(existing);

        PersonalEventResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("Consultation", result.getName());
        assertEquals(2L, result.getUserId());
        verify(personalEventRepository).save(existing);
    }

    @Test
    void updateThrowsWhenPersonalEventDoesNotExist() {
        PersonalEventUpdateDto dto = PersonalEventUpdateDto.builder().id(1L).name("Consultation").build();
        when(personalEventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Personal event not found", exception.getMessage());
        verify(personalEventRepository, never()).save(any(PersonalEvent.class));
    }

    @Test
    void deleteRemovesExistingPersonalEvent() {
        PersonalEvent existing = PersonalEvent.builder().id(1L).userId(2L).build();
        when(personalEventRepository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(personalEventRepository).delete(existing);
    }

    @Test
    void deleteThrowsWhenPersonalEventDoesNotExist() {
        when(personalEventRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Personal event not found", exception.getMessage());
        verify(personalEventRepository, never()).delete(any(PersonalEvent.class));
    }

    @Test
    void createThrowsWhenUserDoesNotExist() {
        PersonalEventCreateDto dto = PersonalEventCreateDto.builder().userId(2L).name("Meeting").build();
        when(userRepository.existsById(2L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        assertEquals("User not found", exception.getMessage());
        verify(personalEventRepository, never()).save(any(PersonalEvent.class));
    }

    @Test
    void findAllReturnsOnlyCurrentUsersEventsForStudent() {
        UserPrincipal student = mock(UserPrincipal.class);
        when(student.getId()).thenReturn(5L);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUser()).thenReturn(student);
        when(personalEventRepository.findAllByUserId(5L))
                .thenReturn(List.of(PersonalEvent.builder().id(10L).userId(5L).name("Mine").build()));

        List<PersonalEventResponseDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(5L, result.getFirst().getUserId());
        verify(personalEventRepository).findAllByUserId(5L);
        verify(personalEventRepository, never()).findAll();
    }

    @Test
    void createUsesResolvedUserIdForStudent() {
        PersonalEventCreateDto dto = PersonalEventCreateDto.builder().userId(999L).name("Meeting").build();
        when(currentUserService.resolveUserId(999L)).thenReturn(5L);
        when(userRepository.existsById(5L)).thenReturn(true);
        when(personalEventRepository.save(any(PersonalEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PersonalEventResponseDto result = service.create(dto);

        assertEquals(5L, result.getUserId());
    }

    @Test
    void findByIdThrowsWhenOwnershipCheckFails() {
        PersonalEvent existing = PersonalEvent.builder().id(1L).userId(99L).build();
        when(personalEventRepository.findById(1L)).thenReturn(Optional.of(existing));
        doThrow(new AccessDeniedException("Access denied")).when(currentUserService).verifyOwnership(99L);

        assertThrows(AccessDeniedException.class, () -> service.findById(1L));
    }
}

