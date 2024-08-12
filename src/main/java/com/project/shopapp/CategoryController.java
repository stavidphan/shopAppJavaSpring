package com.project.shopapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {
    // display all categories
    @GetMapping("")  // http://localhost:8080/api/v1/categories?page=1 &limit=10
    public ResponseEntity<String> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return ResponseEntity.ok(String.format("Get all categories with page: %d and limit: %d", page, limit));
    }

    @PostMapping("")
    public ResponseEntity<String> insertCategory() {
        return ResponseEntity.ok("Category created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id) {
        return ResponseEntity.ok("insert category with id: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok("delete category with id: " + id);
    }
}
