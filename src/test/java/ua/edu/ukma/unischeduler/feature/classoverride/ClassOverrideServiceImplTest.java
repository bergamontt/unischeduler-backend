package ua.edu.ukma.unischeduler.feature.classoverride;

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
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClass;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassOverrideServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseClassRepository courseClassRepository;

    @Mock
    private ClassOverrideRepository classOverrideRepository;

    @Mock
    private CurrentUserService currentUserService;

    private ClassOverrideServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().when(currentUserService.isAdmin()).thenReturn(true);
        lenient().when(currentUserService.resolveUserId(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new ClassOverrideServiceImpl(userRepository, courseClassRepository, classOverrideRepository, Mappers.getMapper(ClassOverrideMapper.class), currentUserService);
    }

    @Test
    void createSavesClassOverride() {
        ClassOverrideCreateDto dto = ClassOverrideCreateDto.builder().user(3L).courseClass(2L).locationOverride("Room 101").build();
        when(userRepository.existsById(3L)).thenReturn(true);
        CourseClass courseClass = CourseClass.builder().id(2L).build();
        when(courseClassRepository.findById(2L)).thenReturn(Optional.of(courseClass));
        when(classOverrideRepository.save(any(ClassOverride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassOverrideResponseDto result = service.create(dto);

        assertEquals("Room 101", result.getLocationOverride());
        assertEquals(2L, result.getCourseClass().getId());
        assertEquals(3L, result.getUser());
        verify(classOverrideRepository).save(any(ClassOverride.class));
    }

    @Test
    void findByIdReturnsClassOverride() {
        ClassOverride existing = ClassOverride.builder().id(1L).locationOverride("Room 101").build();
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.of(existing));

        ClassOverrideResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Room 101", result.getLocationOverride());
    }

    @Test
    void findByIdThrowsWhenClassOverrideDoesNotExist() {
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Class override not found", exception.getMessage());
    }

    @Test
    void findAllReturnsClassOverrides() {
        ClassOverride first = ClassOverride.builder().id(1L).locationOverride("Room 101").build();
        ClassOverride second = ClassOverride.builder().id(2L).locationOverride("Room 202").build();
        when(classOverrideRepository.findAll()).thenReturn(List.of(first, second));

        List<ClassOverrideResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(ClassOverrideResponseDto::getId).toList());
        assertEquals(List.of("Room 101", "Room 202"), result.stream().map(ClassOverrideResponseDto::getLocationOverride).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoClassOverrides() {
        when(classOverrideRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        ClassOverrideUpdateDto dto = ClassOverrideUpdateDto.builder().id(1L).locationOverride("Room 202").build();
        CourseClass courseClass = CourseClass.builder().id(2L).build();
        ClassOverride existing = ClassOverride.builder().id(1L).user(3L).courseClass(courseClass)
                .locationOverride("Room 101").build();
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(classOverrideRepository.save(existing)).thenReturn(existing);

        ClassOverrideResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("Room 202", result.getLocationOverride());
        assertEquals(3L, result.getUser());
        assertSame(courseClass, result.getCourseClass());
        verify(classOverrideRepository).save(existing);
    }

    @Test
    void updateThrowsWhenClassOverrideDoesNotExist() {
        ClassOverrideUpdateDto dto = ClassOverrideUpdateDto.builder().id(1L).locationOverride("Room 202").build();
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Class override not found", exception.getMessage());
        verify(classOverrideRepository, never()).save(any(ClassOverride.class));
    }

    @Test
    void deleteRemovesExistingClassOverride() {
        ClassOverride existing = ClassOverride.builder().id(1L).user(3L).build();
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.of(existing));

        service.delete(1L);

        verify(classOverrideRepository).delete(existing);
    }

    @Test
    void deleteThrowsWhenClassOverrideDoesNotExist() {
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Class override not found", exception.getMessage());
        verify(classOverrideRepository, never()).delete(any());
    }

    @Test
    void createThrowsWhenUserDoesNotExist() {
        ClassOverrideCreateDto dto = ClassOverrideCreateDto.builder().user(3L).courseClass(2L).locationOverride("Room 101").build();
        when(userRepository.existsById(3L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        assertEquals("User not found", exception.getMessage());
        verify(classOverrideRepository, never()).save(any(ClassOverride.class));
        verifyNoInteractions(courseClassRepository);
    }

    @Test
    void createThrowsWhenCourseClassDoesNotExist() {
        ClassOverrideCreateDto dto = ClassOverrideCreateDto.builder().user(3L).courseClass(2L).locationOverride("Room 101").build();
        when(userRepository.existsById(3L)).thenReturn(true);
        when(courseClassRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        assertEquals("Course class not found", exception.getMessage());
        verify(classOverrideRepository, never()).save(any(ClassOverride.class));
    }

    @Test
    void findAllReturnsOnlyCurrentUsersOverridesForStudent() {
        UserPrincipal student = mock(UserPrincipal.class);
        when(student.getId()).thenReturn(5L);
        when(currentUserService.isAdmin()).thenReturn(false);
        when(currentUserService.getCurrentUser()).thenReturn(student);
        when(classOverrideRepository.findAllByUser(5L))
                .thenReturn(List.of(ClassOverride.builder().id(10L).user(5L).build()));

        List<ClassOverrideResponseDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(5L, result.getFirst().getUser());
        verify(classOverrideRepository).findAllByUser(5L);
        verify(classOverrideRepository, never()).findAll();
    }

    @Test
    void createUsesResolvedUserIdForStudent() {
        ClassOverrideCreateDto dto = ClassOverrideCreateDto.builder().user(999L).courseClass(2L).build();
        when(currentUserService.resolveUserId(999L)).thenReturn(5L);
        when(userRepository.existsById(5L)).thenReturn(true);
        when(courseClassRepository.findById(2L)).thenReturn(Optional.of(CourseClass.builder().id(2L).build()));
        when(classOverrideRepository.save(any(ClassOverride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassOverrideResponseDto result = service.create(dto);

        assertEquals(5L, result.getUser());
    }

    @Test
    void findByIdThrowsWhenOwnershipCheckFails() {
        ClassOverride existing = ClassOverride.builder().id(1L).user(99L).build();
        when(classOverrideRepository.findById(1L)).thenReturn(Optional.of(existing));
        doThrow(new AccessDeniedException("Access denied")).when(currentUserService).verifyOwnership(99L);

        assertThrows(AccessDeniedException.class, () -> service.findById(1L));
    }
}

