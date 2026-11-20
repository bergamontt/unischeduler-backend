package ua.edu.ukma.unischeduler.feature.courseclass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.unischeduler.common.exceptions.ResourceNotFoundException;
import ua.edu.ukma.unischeduler.feature.course.Course;
import ua.edu.ukma.unischeduler.feature.course.CourseRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseClassServiceImplTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseClassRepository courseClassRepository;

    private CourseClassServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CourseClassServiceImpl(courseRepository, courseClassRepository, Mappers.getMapper(CourseClassMapper.class));
    }

    @Test
    void createSavesCourseClass() {
        CourseClassCreateDto dto = CourseClassCreateDto.builder().course(2L).location("Room 101").build();
        Course course = Course.builder().id(2L).build();
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(courseClassRepository.save(any(CourseClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CourseClassResponseDto result = service.create(dto);

        assertEquals("Room 101", result.getLocation());
        assertEquals(2L, result.getCourse().getId());
        verify(courseClassRepository).save(any(CourseClass.class));
    }

    @Test
    void findByIdReturnsCourseClass() {
        CourseClass existing = CourseClass.builder().id(1L).location("Room 101").build();
        when(courseClassRepository.findById(1L)).thenReturn(Optional.of(existing));

        CourseClassResponseDto result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Room 101", result.getLocation());
    }

    @Test
    void findByIdThrowsWhenCourseClassDoesNotExist() {
        when(courseClassRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        assertEquals("Course class not found", exception.getMessage());
    }

    @Test
    void findAllReturnsCourseClasss() {
        CourseClass first = CourseClass.builder().id(1L).location("Room 101").build();
        CourseClass second = CourseClass.builder().id(2L).location("Room 202").build();
        when(courseClassRepository.findAll()).thenReturn(List.of(first, second));

        List<CourseClassResponseDto> result = service.findAll();

        assertEquals(List.of(1L, 2L), result.stream().map(CourseClassResponseDto::getId).toList());
        assertEquals(List.of("Room 101", "Room 202"), result.stream().map(CourseClassResponseDto::getLocation).toList());
    }

    @Test
    void findAllReturnsEmptyListWhenThereAreNoCourseClasss() {
        when(courseClassRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void updateSavesChanges() {
        CourseClassUpdateDto dto = CourseClassUpdateDto.builder().id(1L).course(2L).location("Room 202").build();
        CourseClass existing = CourseClass.builder().id(1L).location("Room 101").build();
        when(courseClassRepository.findById(1L)).thenReturn(Optional.of(existing));
        Course course = Course.builder().id(2L).build();
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course));
        when(courseClassRepository.save(existing)).thenReturn(existing);

        CourseClassResponseDto result = service.update(dto);

        assertEquals(1L, result.getId());
        assertEquals("Room 202", result.getLocation());
        assertSame(course, existing.getCourse());
        verify(courseClassRepository).save(existing);
    }

    @Test
    void updateThrowsWhenCourseClassDoesNotExist() {
        CourseClassUpdateDto dto = CourseClassUpdateDto.builder().id(1L).course(2L).location("Room 202").build();
        when(courseClassRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Course class not found", exception.getMessage());
        verify(courseClassRepository, never()).save(any(CourseClass.class));
    }

    @Test
    void deleteRemovesExistingCourseClass() {
        when(courseClassRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(courseClassRepository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenCourseClassDoesNotExist() {
        when(courseClassRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        assertEquals("Course class not found", exception.getMessage());
        verify(courseClassRepository, never()).deleteById(any());
    }

    @Test
    void createThrowsWhenCourseDoesNotExist() {
        CourseClassCreateDto dto = CourseClassCreateDto.builder().course(2L).location("Room 101").build();
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.create(dto));

        assertEquals("Course not found", exception.getMessage());
        verify(courseClassRepository, never()).save(any(CourseClass.class));
    }

    @Test
    void updateThrowsWhenCourseDoesNotExist() {
        CourseClassUpdateDto dto = CourseClassUpdateDto.builder().id(1L).course(2L).location("Room 202").build();
        CourseClass existing = CourseClass.builder().id(1L).location("Room 101").build();
        when(courseClassRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

        assertEquals("Course not found", exception.getMessage());
        assertEquals("Room 101", existing.getLocation());
        verify(courseClassRepository, never()).save(any(CourseClass.class));
    }
}

