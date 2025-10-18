package org.crochet.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.client.MainServiceClient;
import org.crochet.blog.exception.ResourceNotFoundException;
import org.crochet.blog.mapper.FileMapper;
import org.crochet.blog.mapper.PaginationMapper;
import org.crochet.blog.mapper.PostMapper;
import org.crochet.blog.model.Post;
import org.crochet.blog.model.Category;
import org.crochet.blog.payload.UserInfo;
import org.crochet.blog.payload.UserResponse;
import org.crochet.blog.payload.request.PostRequest;
import org.crochet.blog.payload.response.PaginationResponse;
import org.crochet.blog.payload.response.PostResponse;
import org.crochet.blog.repository.PostRepository;
import org.crochet.blog.service.CategoryService;
import org.crochet.blog.service.PostService;
import org.crochet.blog.util.ImageUtils;
import org.crochet.blog.util.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final MainServiceClient mainServiceClient;

    @Transactional
    @Override
    public void createOrUpdatePost(PostRequest request) {
        Post post;

        if (!ObjectUtils.hasText(request.getId())) {
            // Create a new post
            Category category = null;
            if (request.getCategoryId() != null) {
                category = categoryService.getById(request.getCategoryId());
            }

            var images = ImageUtils.sortFiles(request.getFiles());
            post = Post.builder()
                    .category(category)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .home(request.isHome())
                    .files(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            // Update existing post
            post = getById(request.getId());
            post = PostMapper.INSTANCE.partialUpdate(request, post);
        }

        postRepository.save(post);
    }

    @Override
    public PaginationResponse<PostResponse> getPosts(int offset, int limit, String sortBy, String sortDir,
            Specification<Post> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);

        Page<Post> page = postRepository.findAll(spec, pageable);
        List<PostResponse> responses = PostMapper.INSTANCE.toResponses(page.getContent());

        // Enrich with user info
        enrichPostsWithUserInfo(responses);

        return PaginationMapper.toPagination(page, responses);
    }

    @Override
    public PostResponse getDetail(String id) {
        Post post = postRepository.getDetail(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Blog post not found with id: " + id,
                        404));

        PostResponse response = PostMapper.INSTANCE.toResponse(post);

        // Enrich with user info
        enrichPostWithUserInfo(response);

        return response;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<PostResponse> getLimitedPosts() {
        // For now, return home posts. In the future, this could use settings like the
        // main service
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Post> posts = postRepository.findByHomeTrueOrderByCreatedDateDesc(pageable);

        List<PostResponse> responses = PostMapper.INSTANCE.toResponses(posts);

        // Enrich with user info
        enrichPostsWithUserInfo(responses);

        return responses;
    }

    @Override
    public List<String> getPostIds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return postRepository.getPostIds(pageable);
    }

    @Transactional
    @Override
    public void deletePost(String id) {
        Post post = getById(id);
        postRepository.delete(post);
    }

    @Override
    public Post getById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Blog post not found with id: " + id,
                        404));
    }

    /**
     * Enrich a single post with user info
     */
    private void enrichPostWithUserInfo(PostResponse response) {
        if (response.getCreatedBy() != null) {
            try {
                UserResponse userResponse = mainServiceClient.getUserInfo(response.getCreatedBy());
                response.setUserId(userResponse.getId());
                response.setUsername(userResponse.getName());
                response.setUserAvatar(userResponse.getImageUrl());
            } catch (Exception e) {
                log.warn("Failed to get user info for user {}: {}", response.getCreatedBy(), e.getMessage());
            }
        }
    }

    /**
     * Enrich multiple posts with user info efficiently
     */
    private void enrichPostsWithUserInfo(List<PostResponse> responses) {
        if (responses.isEmpty()) {
            return;
        }

        // Get unique user IDs
        List<String> userIds = responses.stream()
                .map(PostResponse::getCreatedBy)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (userIds.isEmpty()) {
            return;
        }

        try {
            // Batch get user info
            List<UserResponse> userResponses = mainServiceClient.getBatchUserInfo(userIds);

            // Create a user map
            Map<String, UserResponse> userMap = userResponses.stream()
                    .collect(Collectors.toMap(UserResponse::getId, user -> user));

            // Enrich responses
            for (PostResponse response : responses) {
                if (response.getCreatedBy() != null) {
                    UserResponse userResponse = userMap.get(response.getCreatedBy());
                    if (userResponse != null) {
                        response.setUserId(userResponse.getId());
                        response.setUsername(userResponse.getName());
                        response.setUserAvatar(userResponse.getImageUrl());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get batch user info: {}", e.getMessage());
        }
    }
}
