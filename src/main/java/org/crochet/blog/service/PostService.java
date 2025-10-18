package org.crochet.blog.service;

import org.crochet.blog.model.Post;
import org.crochet.blog.payload.request.PostRequest;
import org.crochet.blog.payload.response.PostResponse;
import org.crochet.blog.payload.response.PaginationResponse;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PostService {
    void createOrUpdatePost(PostRequest request);

    PaginationResponse<PostResponse> getPosts(int offset, int limit, String sortBy, String sortDir,
                                      Specification<Post> spec);

    PostResponse getDetail(String id);

    List<PostResponse> getLimitedPosts();

    List<String> getPostIds(int offset, int limit);

    void deletePost(String id);

    Post getById(String id);
}
