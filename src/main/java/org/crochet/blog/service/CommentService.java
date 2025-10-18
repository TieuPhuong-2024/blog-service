package org.crochet.blog.service;

import org.crochet.blog.payload.request.CommentRequest;
import org.crochet.blog.payload.response.CommentResponse;
import org.crochet.blog.payload.response.PaginationResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createOrUpdate(CommentRequest request);

    // Post Comments
    // Get root comments for a post (excluding replies)
    PaginationResponse<CommentResponse> getRootCommentsByPost(String postId, int pageNo, int pageSize);

    // Get all comments for a post (including root and replies)
    PaginationResponse<CommentResponse> getCommentsByPost(String postId, int pageNo, int pageSize);

    // Count root comments for a post
    long countRootCommentsByPost(String postId);

    // Count all comments for a post
    long countCommentsByPost(String postId);

    // Common methods
    // Get replies for a specific comment
    List<CommentResponse> getRepliesByCommentId(String commentId);

    // Delete a comment
    void deleteComment(String commentId);
}
