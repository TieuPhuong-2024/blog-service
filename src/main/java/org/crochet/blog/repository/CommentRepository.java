package org.crochet.blog.repository;

import org.crochet.blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Comment entities.
 * Provides methods for retrieving, counting, and managing comments for blog posts.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    /**
     * Retrieves all root comments (comments without a parent) for a specific post with pagination support.
     *
     * @param postId The ID of the post
     * @param pageable Pagination information
     * @return A page of root comments for the specified post
     */
    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(String postId, Pageable pageable);

    /**
     * Retrieves all replies for a specific parent comment, ordered by creation date (ascending).
     *
     * @param parentId The ID of the parent comment
     * @return A list of reply comments
     */
    List<Comment> findByParentIdOrderByCreatedAtAsc(String parentId);

    /**
     * Retrieves all comments (both root comments and replies) for a specific post with pagination support.
     *
     * @param postId The ID of the post
     * @param pageable Pagination information
     * @return A page of all comments for the specified post
     */
    Page<Comment> findByPostIdOrderByCreatedAtDesc(String postId, Pageable pageable);

    /**
     * Counts the number of replies for a specific parent comment.
     *
     * @param parentId The ID of the parent comment
     * @return The number of replies
     */
    long countByParentId(String parentId);

    /**
     * Counts the number of root comments (comments without a parent) for a specific post.
     *
     * @param postId The ID of the post
     * @return The number of root comments
     */
    long countByPostIdAndParentIsNull(String postId);

    /**
     * Counts the total number of comments (both root comments and replies) for a specific post.
     *
     * @param postId The ID of the post
     * @return The total number of comments
     */
    long countByPostId(String postId);

    /**
     * Counts the number of comments for multiple posts.
     *
     * @param postIds A list of post IDs
     * @return A list of Object arrays containing post ID and comment count pairs
     */
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<String> postIds);

    /**
     * Counts the number of replies for multiple parent comments.
     *
     * @param parentIds A list of parent comment IDs
     * @return A list of Object arrays containing parent ID and reply count pairs
     */
    @Query("SELECT c.parent.id, COUNT(c) FROM Comment c WHERE c.parent.id IN :parentIds GROUP BY c.parent.id")
    List<Object[]> countRepliesByParentIds(@Param("parentIds") List<String> parentIds);
}
