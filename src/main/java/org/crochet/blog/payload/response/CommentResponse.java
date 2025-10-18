package org.crochet.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommentResponse {
    private String id;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
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
    public CommentResponse(String id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }
}
