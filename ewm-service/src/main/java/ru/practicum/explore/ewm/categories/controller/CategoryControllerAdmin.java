package ru.practicum.explore.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.categories.dto.CategoryDto;
import ru.practicum.explore.ewm.categories.dto.CategoryDtoNew;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryControllerAdmin {
    private final CategoryMapper mapper;
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryDtoNew newCategory) {
        log.info("Admin: add new category name={}", newCategory.getName());
        return mapper.fromCategoryToDto(service.addCategory(mapper.fromDtoNewToCategory(newCategory)));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer catId) {
        log.info("Admin: delete category with id:{}", catId);
        service.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Integer catId,
                                      @RequestBody @Valid CategoryDtoNew updateCategory) {
        log.info("Admin: update category with id:{}, new name ={}", catId, updateCategory.getName());
        return mapper.fromCategoryToDto(service.update(catId, mapper.fromDtoNewToCategory(updateCategory)));
    }

}
