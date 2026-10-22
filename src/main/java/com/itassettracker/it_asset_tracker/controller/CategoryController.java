package com.itassettracker.it_asset_tracker.controller;
import com.itassettracker.it_asset_tracker.dto.*;
import com.itassettracker.it_asset_tracker.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }
    @GetMapping @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<CategoryResponse> all() { return service.all(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) { return service.create(request); }
}
