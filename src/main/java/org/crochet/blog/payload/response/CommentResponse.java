package org.crochet.blog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentResponse {
    private String id;
    private String content;
    private Instant createdAt;
    private String userId;
    private String username;
    private String userAvatar;

    // Parent-child comment info
    private String parentId;
    private List<CommentResponse> replies;
    private long replyCount;

    // Mentioned user info
    private String mentionedUserId;
    private String mentionedUsername;

    // Constructor for query
    public CommentResponse(String id, String content, Instant createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }
}
