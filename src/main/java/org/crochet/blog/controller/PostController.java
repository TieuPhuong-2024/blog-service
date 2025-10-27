package org.crochet.blog.controller;

import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.crochet.blog.model.Post;
import org.crochet.blog.payload.request.PostRequest;
import org.crochet.blog.payload.response.PaginationResponse;
import org.crochet.blog.payload.response.PostResponse;
import org.crochet.blog.payload.response.ResponseData;
import org.crochet.blog.service.PostService;
import org.crochet.blog.util.ResponseUtil;
import org.springframework.data.jpa.domain.Specification;
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
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Create or update a blog post")
    @ApiResponse(responseCode = "200", description = "Blog post created or updated successfully")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @SecurityRequirement(name = "BearerAuth")
    public ResponseData<String> createOrUpdatePost(
            @RequestBody PostRequest request) {
        postService.createOrUpdatePost(request);
        return ResponseUtil.success("Blog post created or updated successfully");
    }

    @Operation(summary = "Get paginated list of blog posts")
    @ApiResponse(responseCode = "200",
            description = "List of blog posts",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginationResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseData<PaginationResponse<PostResponse>> getPosts(
            @Parameter(description = "Page number (default: 0)")
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @Parameter(description = "Page size (default: 12)")
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize,
            @Parameter(description = "Sort by field (default: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @Parameter(description = "Sort direction (default: DESC)")
            @RequestParam(value = "sortDir", defaultValue = "DESC", required = false) String sortDir,
            @Parameter(description = "Category ID")
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @Parameter(description = "Filter specification")
            @Filter Specification<Post> spec) {
        var response = postService.getPosts(pageNo, pageSize, sortBy, sortDir, categoryId, spec);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get details of a blog post")
    @ApiResponse(responseCode = "200",
            description = "Details of a blog post",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseData<PostResponse> getDetail(@PathVariable("id") String id) {
        var response = postService.getDetail(id);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Get limited blog posts (for home page)")
    @ApiResponse(responseCode = "200", description = "Limited list of blog posts")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/limited")
    public ResponseData<List<PostResponse>> getLimitedPosts() {
        var response = postService.getLimitedPosts();
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Delete a blog post")
    @ApiResponse(responseCode = "200", description = "Blog post deleted successfully")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseData<String> deletePost(@PathVariable("id") String id) {
        postService.deletePost(id);
        return ResponseUtil.success("Blog post deleted successfully");
    }

    @Operation(summary = "Get similar blog posts")
    @ApiResponse(responseCode = "200",
            description = "List of similar blog posts",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/similar")
    public ResponseData<List<PostResponse>> getSimilarPosts(
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of similar posts to return (default: 5)")
            @RequestParam(value = "limit", defaultValue = "5", required = false) int limit) {
        var response = postService.getSimilarPosts(id, limit);
        return ResponseUtil.success(response);
    }
}
