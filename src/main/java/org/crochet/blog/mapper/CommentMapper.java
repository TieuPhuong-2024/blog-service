package org.crochet.blog.mapper;

import org.crochet.blog.model.Comment;
import org.crochet.blog.payload.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);    
    
    @Mapping(target = "parentId", expression = "java(comment.getParent() != null ? comment.getParent().getId() : null)")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "userAvatar", ignore = true)
    @Mapping(target = "mentionedUsername", ignore = true)
    CommentResponse toResponse(Comment comment);
}
