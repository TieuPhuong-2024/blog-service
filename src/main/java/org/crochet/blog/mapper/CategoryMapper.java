package org.crochet.blog.mapper;

import org.crochet.blog.model.Category;
import org.crochet.blog.payload.request.CategoryRequest;
import org.crochet.blog.payload.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper
        extends AbstractMapper<Category, CategoryResponse>, PartialUpdate<Category, CategoryRequest> {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
}
