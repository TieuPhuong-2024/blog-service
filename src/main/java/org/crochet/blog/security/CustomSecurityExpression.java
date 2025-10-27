package org.crochet.blog.security;

import org.crochet.blog.repository.CommentRepository;
import org.crochet.blog.repository.PostRepository;
import org.crochet.blog.util.SecurityUtil;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("customSecurityExpression")
@RequiredArgsConstructor
public class CustomSecurityExpression {
    
    private final CommentRepository commentRepo;
    private final PostRepository postRepo;


    /**
     * Checks if the current user is the owner of the comment or an admin.
     * 
     * @param commentId ID of comment
     * @return true if the current user is the owner of the comment or an admin, false otherwise
     */
    public boolean isCommentOwnerOrAdmin(String commentId) {

        var currentUser = SecurityUtil.getCurrentUser();

        if (currentUser.getRoles().contains("ADMIN")) {
            return true;
        }

        var comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return comment.getUserId().equals(currentUser.getId());
    }

    /**
     * Checks if the current user is the owner of the post or an admin.
     * 
     * @param postId ID of post
     * @return true if the current user is the owner of the post or an admin, false otherwise
     */
    public boolean isPostOwnerOrAdmin(String postId) {
        var currentUser = SecurityUtil.getCurrentUser();
            
        if (currentUser.getRoles().contains("ADMIN")) {
            return true;
        }
        
        var post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return currentUser.getId().equals(post.getCreatedBy());
    }
}
