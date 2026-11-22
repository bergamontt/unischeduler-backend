package ua.edu.ukma.unischeduler.feature.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.classoverride.ClassOverrideRepository;
import ua.edu.ukma.unischeduler.feature.courseclass.CourseClassRepository;
import ua.edu.ukma.unischeduler.feature.speciality.Speciality;
import ua.edu.ukma.unischeduler.feature.speciality.SpecialityRepository;
import ua.edu.ukma.unischeduler.feature.studentcourse.StudentCourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseClassRepository courseClassRepository;

    @Mock
    private ClassOverrideRepository classOverrideRepository;

    @Mock
    private StudentCourseRepository studentCourseRepository;

    private CourseServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CourseServiceImpl(
                specialityRepository,
                courseRepository,
                courseClassRepository,
                classOverrideRepository,
                studentCourseRepository,
                Mappers.getMapper(CourseMapper.class)
        );
    }

    @Test
    void createSavesCourse() {
        CourseCreateDto dto = CourseCreateDto.builder().name("Algorithms").specialities(Set.of(2L)).build();
        Speciality speciality = Speciality.builder().id(2L).build();
        when(specialityRepository.findAllById(Set.of(2L))).thenReturn(List.of(speciality));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponseDto result = service.create(dto);

        assertEquals("Algorithms", result.getName());
        assertEquals(1, result.getSpecialities().size());
        assertEquals(2L, result.getSpecialities().iterator().next().getId());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void findByIdReturnsCourse() {
        Course existing = Course.builder().id(1L).name("Algorithms").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));

        CourseResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Algorithms", result.getName());
    }

    @Test
    void findByIdThrowsWhenCourseDoesNotExist() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void findAllReturnsCourses() {
        Course first = Course.builder().id(1L).name("Algorithms").build();
        Course second = Course.builder().id(2L).name("Databases").build();
        when(courseRepository.findAll()).thenReturn(List.of(first, second));

        List<CourseResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(CourseResponseDto::getId).toList());
        assertEquals(List.of("Algorithms", "Databases"), result.stream().map(CourseResponseDto::getName).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoCourses() {
        when(courseRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        CourseUpdateDto dto = CourseUpdateDto.builder().id(1L).name("Databases").specialities(Set.of(2L)).build();
        Course existing = Course.builder().id(1L).name("Algorithms").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        Speciality speciality = Speciality.builder().id(2L).build();
        when(specialityRepository.findAllById(Set.of(2L))).thenReturn(List.of(speciality));
        when(courseRepository.save(existing)).thenReturn(existing);

        CourseResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("Databases", result.getName());
        assertEquals(Set.of(speciality), existing.getSpecialities());
        verify(courseRepository).save(existing);
    }

    @Test
    void updateThrowsWhenCourseDoesNotExist() {
        CourseUpdateDto dto = CourseUpdateDto.builder().id(1L).name("Databases").specialities(Set.of(2L)).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Course not found", exception.getMessage());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteRemovesExistingCourse() {
        Course existing = Course.builder().id(1L).name("Algorithms").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseClassRepository.findAllByCourseId(1L)).thenReturn(List.of());

        service.delete(1L);

        verify(studentCourseRepository).deleteAllByCourseId(1L);
        verify(courseClassRepository).deleteAll(List.of());
        verify(courseRepository).delete(existing);
    }

    @Test
    void deleteThrowsWhenCourseDoesNotExist() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Course not found", exception.getMessage());
        verify(courseRepository, never()).delete(any());
    }
}
