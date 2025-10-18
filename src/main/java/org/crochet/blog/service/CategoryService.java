package org.crochet.blog.service;

import org.crochet.blog.model.Category;
import org.crochet.blog.payload.request.CategoryRequest;
import org.crochet.blog.payload.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    void createOrUpdate(CategoryRequest request);

    CategoryResponse getDetail(String id);

    List<CategoryResponse> getAll();

    void delete(String id);

    Category getById(String id);
}
