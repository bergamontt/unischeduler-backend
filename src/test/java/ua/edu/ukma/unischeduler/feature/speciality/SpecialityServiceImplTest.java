package ua.edu.ukma.unischeduler.feature.speciality;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;
import ua.edu.ukma.unischeduler.feature.course.CourseService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialityServiceImplTest {
    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseService courseService;

    private SpecialityServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SpecialityServiceImpl(
                specialityRepository,
                Mappers.getMapper(SpecialityMapper.class),
                courseRepository,
                courseService
        );
    }

    @Test
    void createSavesSpeciality() {
        SpecialityCreateDto dto = new SpecialityCreateDto("Computer Science");
        when(specialityRepository.save(any(Speciality.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SpecialityResponseDto result = service.create(dto);

        assertEquals("Computer Science", result.getName());
        verify(specialityRepository).save(any(Speciality.class));
    }

    @Test
    void findByIdReturnsSpeciality() {
        Speciality existing = Speciality.builder().id(1L).name("Computer Science").build();
        when(specialityRepository.findById(1L)).thenReturn(Optional.of(existing));

        SpecialityResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Computer Science", result.getName());
    }

    @Test
    void findByIdThrowsWhenSpecialityDoesNotExist() {
        when(specialityRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Speciality not found", exception.getMessage());
    }

    @Test
    void findAllReturnsSpecialitys() {
        Speciality first = Speciality.builder().id(1L).name("Computer Science").build();
        Speciality second = Speciality.builder().id(2L).name("Software Engineering").build();
        when(specialityRepository.findAll()).thenReturn(List.of(first, second));

        List<SpecialityResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(SpecialityResponseDto::getId).toList());
        assertEquals(List.of("Computer Science", "Software Engineering"), result.stream().map(SpecialityResponseDto::getName).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoSpecialitys() {
        when(specialityRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        SpecialityUpdateDto dto = new SpecialityUpdateDto(1L, "Software Engineering");
        Speciality existing = Speciality.builder().id(1L).name("Computer Science").build();
        when(specialityRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(specialityRepository.save(existing)).thenReturn(existing);

        SpecialityResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("Software Engineering", result.getName());
        verify(specialityRepository).save(existing);
    }

    @Test
    void updateThrowsWhenSpecialityDoesNotExist() {
        SpecialityUpdateDto dto = new SpecialityUpdateDto(1L, "Software Engineering");
        when(specialityRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Speciality not found", exception.getMessage());
        verify(specialityRepository, never()).save(any(Speciality.class));
    }

    @Test
    void deleteRemovesExistingSpecialityAndItsCourses() {
        when(specialityRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findCourseIdsBySpecialityId(1L)).thenReturn(List.of(10L, 20L));

        service.delete(1L);

        verify(courseService).delete(10L);
        verify(courseService).delete(20L);
        verify(specialityRepository).deleteSpecialityByIdNative(1L);
    }

    @Test
    void deleteThrowsWhenSpecialityDoesNotExist() {
        when(specialityRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Speciality not found", exception.getMessage());
        verify(specialityRepository, never()).deleteSpecialityByIdNative(any());
    }
}
