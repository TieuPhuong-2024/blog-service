package org.crochet.blog.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private String id;
    private String name;
    private String imageUrl;
    private String email;
    private RoleType role;
    private Boolean emailVerified;
    private String createdDate;
    private String lastModifiedDate;

    enum RoleType {
        USER,
        VIP_USER,
        ADMIN;
    }
}
