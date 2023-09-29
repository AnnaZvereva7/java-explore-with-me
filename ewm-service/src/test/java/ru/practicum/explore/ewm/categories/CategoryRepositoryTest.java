package ru.practicum.explore.ewm.categories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repository;

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdIn_whenOk() {
        Optional<Category> cat = repository.findById(1);
        assertThat(cat).isPresent();
        assertEquals(1, cat.get().getId());
        assertEquals("category1", cat.get().getName());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findByIdIn_whenEmpty() {
        Optional<Category> cat = repository.findById(6);
        assertThat(cat).isEmpty();
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void add_whenOk() {
        Category newCat = new Category(null, "category5");
        Category actualCat = repository.saveAndFlush(newCat);
        assertEquals(5, actualCat.getId());
        assertEquals("category5", actualCat.getName());
        assertEquals(5, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void add_whenNameNotUnique() {
        Category newCat = new Category(null, "category1");
        Throwable thrown = catchThrowable(() -> {
            repository.saveAndFlush(newCat);
        });
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void delete_whenOk() {
        repository.deleteById(4);
        assertEquals(3, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void update_whenOk() {
        Category cat = new Category(1, "newCategoryName");
        Category updated = repository.saveAndFlush(cat);
        assertEquals(1, updated.getId());
        assertEquals("newCategoryName", updated.getName());
        assertEquals(4, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void update_whenSame() {
        Category cat = new Category(1, "category1");
        Category updated = repository.saveAndFlush(cat);
        assertEquals(1, updated.getId());
        assertEquals("category1", updated.getName());
        assertEquals(4, repository.findAll().size());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllPage_whenOk() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 10, Sort.by("id").ascending());
        List<Category> cats = repository.findAllPage(pageRequest);
        assertEquals(4, cats.size());
        assertEquals(1, cats.get(0).getId());
        assertEquals(2, cats.get(1).getId());
        assertEquals(3, cats.get(2).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllPage_whenFrom1() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(1, 10, Sort.by("id").ascending());
        List<Category> cats = repository.findAllPage(pageRequest);
        assertEquals(3, cats.size());
        assertEquals(2, cats.get(0).getId());
        assertEquals(3, cats.get(1).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllPage_whenSize1() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(0, 1, Sort.by("id").ascending());
        List<Category> cats = repository.findAllPage(pageRequest);
        assertEquals(1, cats.size());
        assertEquals(1, cats.get(0).getId());
    }

    @Test
    @Sql({"/schemaTest.sql", "/import_tables.sql"})
    void findAllPage_whenOutOfAmount() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(4, 4, Sort.by("id").ascending());
        List<Category> cats = repository.findAllPage(pageRequest);
        assertEquals(0, cats.size());
    }
}