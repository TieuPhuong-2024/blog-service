package org.crochet.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.exception.ResourceNotFoundException;
import org.crochet.blog.mapper.CategoryMapper;
import org.crochet.blog.model.Category;
import org.crochet.blog.payload.request.CategoryRequest;
import org.crochet.blog.payload.response.CategoryResponse;
import org.crochet.blog.repository.CategoryRepository;
import org.crochet.blog.service.CategoryService;
import org.crochet.blog.util.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public void createOrUpdate(CategoryRequest request) {
        Category category;

        if (!ObjectUtils.hasText(request.getId())) {
            // Create new category
            category = Category.builder()
                    .name(request.getName())
                    .build();
        } else {
            // Update existing category
            category = getById(request.getId());
            category.setName(request.getName());
        }

        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getDetail(String id) {
        Category category = getById(id);
        return CategoryMapper.INSTANCE.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();
        return CategoryMapper.INSTANCE.toResponses(categories);
    }

    @Transactional
    @Override
    public void delete(String id) {
        Category category = getById(id);
        categoryRepository.delete(category);
    }

    @Override
    public Category getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id));
    }
}
