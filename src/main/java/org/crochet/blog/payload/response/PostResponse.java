package org.crochet.blog.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("is_home")
    private boolean isHome;
    private List<FileResponse> files;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private String fileContent;
    private String createdBy;
    private String userId;
    private String username;
    private String userAvatar;
    private Long commentCount;
}
