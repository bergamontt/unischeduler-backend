package ua.edu.ukma.unischeduler.feature.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.common.enums.UserRole;
import ua.edu.ukma.unischeduler.feature.auth.CurrentUserService;
import ua.edu.ukma.unischeduler.feature.auth.UserPrincipal;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        UserPrincipal currentAdmin = mock(UserPrincipal.class);
        lenient().when(currentAdmin.getId()).thenReturn(999L);
        lenient().when(currentUserService.getCurrentUser()).thenReturn(currentAdmin);
        service = new UserServiceImpl(specialityRepository, userRepository, Mappers.getMapper(UserMapper.class), currentUserService);
    }

    @Test
    void createSavesUser() {
        UserCreateDto dto = UserCreateDto.builder().specialityId(2L).email("student@ukma.edu.ua").role(UserRole.STUDENT).build();
        Speciality speciality = Speciality.builder().id(2L).build();
        when(specialityRepository.findById(2L)).thenReturn(Optional.of(speciality));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = service.create(dto);

        assertEquals("student@ukma.edu.ua", result.getEmail());
        assertEquals(2L, result.getSpeciality().getId());
        assertEquals(UserRole.STUDENT, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByIdReturnsUser() {
        User existing = User.builder().id(1L).email("student@ukma.edu.ua").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("student@ukma.edu.ua", result.getEmail());
    }

    @Test
    void findByIdThrowsWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findAllReturnsUsers() {
        User first = User.builder().id(1L).email("student@ukma.edu.ua").build();
        User second = User.builder().id(2L).email("updated@ukma.edu.ua").build();
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        List<UserResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(UserResponseDto::getId).toList());
        assertEquals(List.of("student@ukma.edu.ua", "updated@ukma.edu.ua"), result.stream().map(UserResponseDto::getEmail).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        UserUpdateDto dto = UserUpdateDto.builder().id(1L).specialityId(2L).email("updated@ukma.edu.ua").role(UserRole.ADMIN).build();
        User existing = User.builder().id(1L).email("student@ukma.edu.ua").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Speciality speciality = Speciality.builder().id(2L).build();
        when(specialityRepository.findById(2L)).thenReturn(Optional.of(speciality));
        when(userRepository.save(existing)).thenReturn(existing);

        UserResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("updated@ukma.edu.ua", result.getEmail());
        assertEquals(UserRole.ADMIN, result.getRole());
        assertSame(speciality, existing.getSpeciality());
        verify(userRepository).save(existing);
    }

    @Test
    void updateThrowsWhenUserDoesNotExist() {
        UserUpdateDto dto = UserUpdateDto.builder().id(1L).specialityId(2L).email("updated@ukma.edu.ua").role(UserRole.ADMIN).build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteRemovesExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenUserDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void deleteThrowsWhenDeletingOwnAccount() {
        UserPrincipal self = mock(UserPrincipal.class);
        when(self.getId()).thenReturn(1L);
        when(currentUserService.getCurrentUser()).thenReturn(self);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.delete(1L));

        assertEquals("You cannot delete your own account", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void createThrowsWhenSpecialityDoesNotExist() {
        UserCreateDto dto = UserCreateDto.builder().specialityId(2L).email("student@ukma.edu.ua").role(UserRole.STUDENT).build();
        when(specialityRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        assertEquals("Speciality not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateThrowsWhenSpecialityDoesNotExist() {
        UserUpdateDto dto = UserUpdateDto.builder().id(1L).specialityId(2L).email("updated@ukma.edu.ua").role(UserRole.ADMIN).build();
        User existing = User.builder().id(1L).email("student@ukma.edu.ua").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(specialityRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Speciality not found", exception.getMessage());
        assertEquals("student@ukma.edu.ua", existing.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}
