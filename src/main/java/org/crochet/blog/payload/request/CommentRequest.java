package org.crochet.blog.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    // ID for update, not required for create
    private String id;

    // Blog post ID
    private String postId;

    // ID of parent comment (null if root comment)
    private String parentId;

    // Comment content
    @NotBlank(message = "Content cannot be empty")
    private String content;

    // ID of mentioned user (can be null)
    private String mentionedUserId;
}
