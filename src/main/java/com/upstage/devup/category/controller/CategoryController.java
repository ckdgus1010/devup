package com.upstage.devup.category.controller;

import com.upstage.devup.category.dto.CategoryAddRequest;
import com.upstage.devup.category.dto.CategoryDto;
import com.upstage.devup.category.dto.CategoryUpdateRequest;
import com.upstage.devup.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryAddRequest request) {

        return ResponseEntity.ok(
                categoryService.addCategory(request)
        );
    }

    @PatchMapping
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryUpdateRequest request) {

        return ResponseEntity.ok(
                categoryService.updateCategory(request)
        );
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {

        return ResponseEntity.ok(
                categoryService.deleteCategory(categoryId)
        );
    }
}
