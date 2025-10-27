package org.crochet.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.crochet.blog.payload.request.CategoryRequest;
import org.crochet.blog.payload.response.CategoryResponse;
import org.crochet.blog.payload.response.ResponseData;
import org.crochet.blog.service.CategoryService;
import org.crochet.blog.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Create or update a category")
    @ApiResponse(responseCode = "200", description = "Category created or updated successfully")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdate(@RequestBody CategoryRequest request) {
        categoryService.createOrUpdate(request);
        return ResponseUtil.success("Category created or updated successfully");
    }

    @Operation(summary = "Delete a category")
    @ApiResponse(responseCode = "200", description = "Category deleted successfully")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/admin/{id}")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> delete(@PathVariable("id") String id) {
        categoryService.delete(id);
        return ResponseUtil.success("Category deleted successfully");
    }

    @Operation(summary = "Get category details")
    @ApiResponse(responseCode = "200",
            description = "Category details",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<CategoryResponse> getDetail(@PathVariable String id) {
        var response = categoryService.getDetail(id);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get all categories")
    @ApiResponse(responseCode = "200", description = "List of all categories")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<List<CategoryResponse>> getAll() {
        var response = categoryService.getAll();
        return ResponseUtil.success(response);
    }
}
