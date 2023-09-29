package ru.practicum.explore.ewm.categories.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.explore.ewm.categories.dto.CategoryDtoNew;
import ru.practicum.explore.ewm.categories.dto.CategoryMapper;
import ru.practicum.explore.ewm.exceptions.ErrorHandler;
import ru.practicum.explore.ewm.exceptions.NotFoundException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerAdminTest {
    @Mock
    private CategoryMapper mapper;
    @Mock
    private CategoryService service;
    @InjectMocks
    private CategoryControllerAdmin controllerAdmin;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controllerAdmin).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void addCategory_whenNameBlank() throws Exception {
        CategoryDtoNew dtoWrong = new CategoryDtoNew("  ");
        mvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(dtoWrong))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("must be not blank")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addCategory_whenNameLong() throws Exception {
        CategoryDtoNew dtoWrong = new CategoryDtoNew("Category Name Too LongCategory Name Too LongCategory Name Too Long");
        mvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(dtoWrong))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.reason", is("Incorrectly made request.")))
                .andExpect(jsonPath("$.message", is("Max size 50")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void addCategory_whenOk() throws Exception {
        CategoryDtoNew newDto = new CategoryDtoNew("newName");
        Category categoryToAdd = new Category(null, "newName");
        Category category = new Category(1, "categoryName");
        when(mapper.fromDtoNewToCategory(newDto)).thenReturn(categoryToAdd);
        when(service.addCategory(categoryToAdd)).thenReturn(category);
        when(mapper.fromCategoryToDto(category)).thenReturn(new CategoryDto(1, "newName"));
        mvc.perform(post("/admin/categories")
                        .content(objectMapper.writeValueAsString(newDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("newName")));
    }

    @Test
    void updateCategory_whenOk() throws Exception {
        CategoryDtoNew newDto = new CategoryDtoNew("newName");
        Category categoryToAdd = new Category(null, "newName");
        Category category = new Category(1, "categoryName");
        when(mapper.fromDtoNewToCategory(newDto)).thenReturn(categoryToAdd);
        when(service.update(1, categoryToAdd)).thenReturn(category);
        when(mapper.fromCategoryToDto(category)).thenReturn(new CategoryDto(1, "newName"));
        mvc.perform(patch("/admin/categories/{catId}", 1)
                        .content(objectMapper.writeValueAsString(newDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("newName")));
    }

    @Test
    void updateCategory_whenWrongId() throws Exception {
        CategoryDtoNew newDto = new CategoryDtoNew("newName");
        Category categoryToAdd = new Category(null, "newName");
        when(mapper.fromDtoNewToCategory(newDto)).thenReturn(categoryToAdd);
        when(service.update(1, categoryToAdd)).thenThrow(new NotFoundException("Category with id=1 was not found"));

        mvc.perform(patch("/admin/categories/{catId}", 1)
                        .content(objectMapper.writeValueAsString(newDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("NOT_FOUND")))
                .andExpect(jsonPath("$.reason", is("The required object was not found.")))
                .andExpect(jsonPath("$.message", is("Category with id=1 was not found")))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}