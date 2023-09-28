package ru.practicum.explore.ewm.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;
    private CategoryService service;
    private final Category cat = new Category(1, "category1");

    @BeforeEach
    void setup() {
        service = new CategoryService(repository);
    }

    @Test
    void addCategory_whenOk() {
        Category newCat = new Category(null, "category1");
        when(repository.saveAndFlush(newCat)).thenReturn(cat);
        Category actualCat = service.addCategory(newCat);
        assertEquals(1, actualCat.getId());
        assertEquals("category1", actualCat.getName());
    }

    @Test
    void findById_whenOk() {
        when(repository.findById(1)).thenReturn(Optional.of(cat));
        Category actualCat = service.findById(1);
        assertEquals(1, actualCat.getId());
        assertEquals("category1", actualCat.getName());
    }

    @Test
    void findById_whenEmpty() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.findById(1);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenOk() {
        Category newCat = new Category(null, "new");
        Category expectedCat = new Category(1, "new");
        when(repository.findById(1)).thenReturn(Optional.of(cat));
        when(repository.saveAndFlush(expectedCat)).thenReturn(expectedCat);
        Category actualCat = service.update(1, newCat);
        assertEquals(1, actualCat.getId());
        assertEquals("new", actualCat.getName());
    }

    @Test
    void update_whenWrongId() {
        Category newCat = new Category(null, "new");
        when(repository.findById(1)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.update(1, newCat);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenWrongId() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> {
            service.delete(1);
        });
        assertThat(thrown).isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenOk() {
        when(repository.findById(1)).thenReturn(Optional.of(cat));
        doNothing().when(repository).deleteById(1);
        service.delete(1);
        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void findAllPage_whenOk() {
        when(repository.findAllPage(any(OffsetBasedPageRequest.class))).thenReturn(List.of(new Category(1, "categoryName")));
        List<Category> cats = service.getAllPage(0, 10);
        assertEquals(1, cats.size());
        assertEquals(1, cats.get(0).getId());
        assertEquals("categoryName", cats.get(0).getName());
    }
}