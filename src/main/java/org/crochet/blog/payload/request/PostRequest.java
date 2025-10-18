package org.crochet.blog.payload.request;

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
    private String categoryId;
    private String title;
    private String content;
    private boolean showOnHomePage;
    private List<FileResponse> files;
}
