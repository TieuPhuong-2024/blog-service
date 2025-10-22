package org.crochet.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.crochet.blog.payload.request.CommentRequest;
import org.crochet.blog.payload.response.CommentResponse;
import org.crochet.blog.payload.response.PaginationResponse;
import org.crochet.blog.payload.response.ResponseData;
import org.crochet.blog.service.CommentService;
import org.crochet.blog.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post-comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Create or update a comment")
    @ApiResponse(responseCode = "201", description = "Comment created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<CommentResponse> createOrUpdateComment(
            @Valid @RequestBody CommentRequest request) {
        var response = commentService.createOrUpdate(request);
        return ResponseUtil.success(response, "Comment created or updated successfully");
    }

    @Operation(summary = "Get root comments for a blog post")
    @ApiResponse(responseCode = "200", description = "Root comments retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/post/{post_id}/root")
    public ResponseData<PaginationResponse<CommentResponse>> getRootCommentsByBlogPost(
            @Parameter(description = "Blog post ID") @PathVariable("post_id") String postId,
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var response = commentService.getRootCommentsByPost(postId, pageNo, pageSize);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get all comments for a blog post")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/post/{post_id}")
    public ResponseData<PaginationResponse<CommentResponse>> getCommentsByPost(
            @Parameter(description = "Blog post ID") @PathVariable("post_id") String postId,
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @Parameter(description = "Page size (default: 10)")
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var response = commentService.getCommentsByPost(postId, pageNo, pageSize);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get replies for a specific comment")
    @ApiResponse(responseCode = "200", description = "Comment replies retrieved successfully")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{comment_id}/replies")
    public ResponseData<List<CommentResponse>> getRepliesByCommentId(
            @Parameter(description = "Comment ID") @PathVariable("comment_id") String commentId) {
        var response = commentService.getRepliesByCommentId(commentId);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Delete a comment")
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{comment_id}")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> deleteComment(
            @Parameter(description = "Comment ID") @PathVariable("comment_id") String commentId) {
        commentService.deleteComment(commentId);
        return ResponseUtil.success("Comment deleted successfully");
    }
}
