package org.crochet.blog.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.crochet.blog.payload.response.FileResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String id;
    @JsonProperty("category_id")
    private String categoryId;
    private String title;
    private String content;
    @JsonProperty("is_home")
    private boolean isHome;
    private List<FileResponse> files;
}
