package ru.practicum.explore.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.categories.dto.CategoryDto;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerPublic {
    private final CategoryMapper mapper;
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Public: get categories from {} size {}", from, size);
        return service.getAllPage(from, size)
                .stream()
                .map(mapper::fromCategoryToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable int catId) {
        log.info("Public: get category with id={}", catId);
        return mapper.fromCategoryToDto(service.findById(catId));
    }

}
