package ru.practicum.explore.ewm.categories.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.explore.ewm.categories.Category;
import ru.practicum.explore.ewm.categories.CategoryService;
import ru.practicum.explore.ewm.categories.dto.CategoryDto;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerPublicTest {
    @Mock
    private CategoryMapper mapper;
    @Mock
    private CategoryService service;
    @InjectMocks
    CategoryControllerPublic controllerPublic;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controllerPublic).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void getCategories_whenOk() throws Exception {
        Category category = new Category(1, "name");
        when(service.getAllPage(0, 10)).thenReturn(List.of(category));
        when(mapper.fromCategoryToDto(category)).thenReturn(new CategoryDto(1, "name"));
        mvc.perform(get("/categories")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")));
    }

    @Test
    void findById_whenOk() throws Exception {
        Category category = new Category(1, "name");
        CategoryDto categoryDto = new CategoryDto(1, "name");
        when(service.findById(1)).thenReturn(category);
        when(mapper.fromCategoryToDto(category)).thenReturn(categoryDto);
        mvc.perform(get("/categories/{catId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")));
    }
}