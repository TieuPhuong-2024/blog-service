package org.crochet.blog.mapper;

import org.crochet.blog.model.Post;
import org.crochet.blog.payload.request.PostRequest;
import org.crochet.blog.payload.response.PostResponse;
import org.crochet.blog.util.ImageUtils;
import org.crochet.blog.util.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { FileMapper.class })
public interface PostMapper extends PartialUpdate<Post, PostRequest> {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "isHome", source = "home")
    PostResponse toResponse(Post post);

    default List<PostResponse> toResponses(Collection<Post> posts) {
        return Optional.ofNullable(posts)
                .map(blogs -> blogs.stream()
                        .map(this::toResponse)
                        .toList())
                .orElse(null);
    }

    @Override
    default Post partialUpdate(PostRequest request, Post post) {
        if (post == null) {
            return null;
        }
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.isHome() != post.isHome()) {
            post.setHome(request.isHome());
        }
        if (ObjectUtils.isNotEmpty(request.getFiles())) {
            var sortedFiles = ImageUtils.sortFiles(request.getFiles());
            post.setFiles(FileMapper.INSTANCE.toEntities(sortedFiles));
        }
        return post;
    }
}
