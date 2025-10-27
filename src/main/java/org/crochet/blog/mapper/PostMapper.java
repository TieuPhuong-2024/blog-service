package org.crochet.blog.mapper;

import org.crochet.blog.model.Post;
import org.crochet.blog.payload.response.PostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE, 
    uses = {FileMapper.class, CategoryMapper.class, CommentMapper.class}
)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    PostResponse toResponse(Post post);

    List<PostResponse> toResponses(Collection<Post> posts);
}
