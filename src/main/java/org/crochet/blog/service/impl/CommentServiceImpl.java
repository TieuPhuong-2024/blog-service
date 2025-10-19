package org.crochet.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.client.MainServiceClient;
import org.crochet.blog.exception.ResourceNotFoundException;
import org.crochet.blog.mapper.CommentMapper;
import org.crochet.blog.model.Comment;
import org.crochet.blog.model.Post;
import org.crochet.blog.payload.UserResponse;
import org.crochet.blog.payload.request.CommentRequest;
import org.crochet.blog.payload.response.CommentResponse;
import org.crochet.blog.payload.response.PaginationResponse;
import org.crochet.blog.repository.CommentRepository;
import org.crochet.blog.service.PostService;
import org.crochet.blog.service.CommentService;
import org.crochet.blog.util.ObjectUtils;
import org.crochet.blog.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MainServiceClient mainServiceClient;

    @Transactional
    @Override
    public CommentResponse createOrUpdate(CommentRequest request) {
        Comment comment;
        String currentUserId = SecurityUtil.getCurrentUserId();

        if (!ObjectUtils.hasText(request.getId())) {
            // Create a new comment
            Post post = postService.getById(request.getPostId());

            Comment parent = null;
            if (request.getParentId() != null) {
                parent = commentRepository.findById(request.getParentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            }

            comment = Comment.builder()
                    .post(post)
                    .userId(currentUserId)
                    .content(request.getContent())
                    .parent(parent)
                    .mentionedUserId(request.getMentionedUserId())
                    .build();
        } else {
            // Update existing comment
            comment = commentRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

            // Check permission
            if (!currentUserId.equals(comment.getUserId())) {
                throw new RuntimeException("User does not have permission to update this comment");
            }

            comment.setContent(request.getContent());
            comment.setMentionedUserId(request.getMentionedUserId());
        }

        comment = commentRepository.save(comment);

        // Convert to response and enrich with user info
        CommentResponse response = CommentMapper.INSTANCE.toResponse(comment);
        enrichCommentWithUserInfo(response);

        return response;
    }

    @Override
    public PaginationResponse<CommentResponse> getRootCommentsByPost(String postId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Comment> page = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(
                postId, pageable);

        List<CommentResponse> responses = page.getContent().stream()
                .map(CommentMapper.INSTANCE::toResponse)
                .toList();

        // Enrich with user info and replies
        enrichCommentsWithUserInfo(responses);
        addReplyCounts(responses);

        return PaginationResponse.<CommentResponse>builder()
            .contents(responses)
            .pageNo(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    @Override
    public PaginationResponse<CommentResponse> getCommentsByPost(String postId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Comment> page = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);

        List<CommentResponse> responses = page.getContent().stream()
                .map(CommentMapper.INSTANCE::toResponse)
                .toList();

        // Enrich with user info
        enrichCommentsWithUserInfo(responses);

        return PaginationResponse.<CommentResponse>builder()
            .contents(responses)
            .pageNo(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }

    @Override
    public long countRootCommentsByPost(String postId) {
        return commentRepository.countByPostIdAndParentIsNull(postId);
    }

    @Override
    public long countCommentsByPost(String postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public List<CommentResponse> getRepliesByCommentId(String commentId) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);

        List<CommentResponse> responses = replies.stream()
                .map(CommentMapper.INSTANCE::toResponse)
                .toList();

        // Enrich with user info
        enrichCommentsWithUserInfo(responses);

        return responses;
    }

    @Transactional
    @Override
    public void deleteComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        String currentUserId = SecurityUtil.getCurrentUserId();
        if (!currentUserId.equals(comment.getUserId())) {
            throw new RuntimeException("User does not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }


    /**
     * Enrich a single comment with user info
     */
    private void enrichCommentWithUserInfo(CommentResponse response) {
        if (response.getUserId() != null) {
            try {
                UserResponse userResponse = mainServiceClient.getUserInfo(response.getUserId());
                response.setUsername(userResponse.getName());
                response.setUserAvatar(userResponse.getImageUrl());
            } catch (Exception e) {
                log.warn("Failed to get user info for user {}: {}", response.getUserId(), e.getMessage());
            }
        }

        if (response.getMentionedUserId() != null) {
            try {
                UserResponse mentionedUser = mainServiceClient.getUserInfo(response.getMentionedUserId());
                response.setMentionedUsername(mentionedUser.getName());
            } catch (Exception e) {
                log.warn("Failed to get mentioned user info for user {}: {}", response.getMentionedUserId(), e.getMessage());
            }
        }
    }

    /**
     * Enrich multiple comments with user info efficiently
     */
    private void enrichCommentsWithUserInfo(List<CommentResponse> responses) {
        if (responses.isEmpty()) {
            return;
        }

        // Get unique user IDs (both comment authors and mentioned users)
        List<String> userIds = responses.stream()
                .flatMap(response -> {
                    List<String> ids = new java.util.ArrayList<>();
                    if (response.getUserId() != null) ids.add(response.getUserId());
                    if (response.getMentionedUserId() != null) ids.add(response.getMentionedUserId());
                    return ids.stream();
                })
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
            for (CommentResponse response : responses) {
                if (response.getUserId() != null) {
                    UserResponse userResponse = userMap.get(response.getUserId());
                    if (userResponse != null) {
                        response.setUsername(userResponse.getName());
                        response.setUserAvatar(userResponse.getImageUrl());
                    }
                }

                if (response.getMentionedUserId() != null) {
                    UserResponse mentionedUser = userMap.get(response.getMentionedUserId());
                    if (mentionedUser != null) {
                        response.setMentionedUsername(mentionedUser.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get batch user info: {}", e.getMessage());
        }
    }

    /**
     * Add reply counts to root comments
     */
    private void addReplyCounts(List<CommentResponse> responses) {
        List<String> commentIds = responses.stream()
                .map(CommentResponse::getId)
                .toList();

        if (!commentIds.isEmpty()) {
            try {
                List<Object[]> replyCounts = commentRepository.countRepliesByParentIds(commentIds);

                Map<String, Long> replyCountMap = replyCounts.stream()
                        .collect(Collectors.toMap(
                                result -> (String) result[0], // parentId
                                result -> (Long) result[1]    // count
                        ));

                responses.forEach(response ->
                    response.setReplyCount(replyCountMap.getOrDefault(response.getId(), 0L)));
            } catch (Exception e) {
                log.warn("Failed to get reply counts: {}", e.getMessage());
            }
        }
    }
}
