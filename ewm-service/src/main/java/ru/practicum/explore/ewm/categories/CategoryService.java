package ru.practicum.explore.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.ewm.common.OffsetBasedPageRequest;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;

    public Category addCategory(Category cat) {
        return repository.saveAndFlush(cat);
    }

    public Category findById(Integer catId) {
        return repository.findById(catId).orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
    }

    public Category update(Integer catId, Category cat) {
        Category category = findById(catId);
        category.setName(cat.getName());
        return repository.saveAndFlush(category);
    }

    public void delete(Integer catId) {
        findById(catId);
        repository.deleteById(catId);
    }

    public List<Category> getAllPage(int from, int size) {
        return repository.findAllPage(new OffsetBasedPageRequest(from, size, Sort.by("id").ascending()));
    }
}
