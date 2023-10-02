package ru.practicum.explore.ewm.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    //todo проверка на уникальность без учета регистра и лишних пробелов

    @Query(value = "select c from Category c")
    List<Category> findAllPage(OffsetBasedPageRequest pageRequest);
}
