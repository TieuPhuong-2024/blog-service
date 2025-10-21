package org.crochet.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private boolean showOnHome;
    private List<FileResponse> files;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private String fileContent;
    private String createdBy;
    private String userId;
    private String username;
    private String userAvatar;
    private Long commentCount;

    public PostResponse(String id,
                        String title,
                        String content,
                        String fileContent,
                        Instant createdAt,
                        String createdBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileContent = fileContent;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public PostResponse(String id,
                        String title,
                        String content,
                        String fileContent,
                        Instant createdAt,
                        String createdBy,
                        String userId,
                        String username,
                        String userAvatar) {
        this(id, title, content, fileContent, createdAt, createdBy);
        this.userId = userId;
        this.username = username;
        this.userAvatar = userAvatar;
    }
}
