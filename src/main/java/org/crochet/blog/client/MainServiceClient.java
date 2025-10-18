package org.crochet.blog.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.payload.UserInfo;
import org.crochet.blog.payload.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceClient {

    private final RestClient restClient;

    @Value("${main-service.url}")
    private String mainServiceUrl;

    @Value("${main-service.internal-api-key}")
    private String internalApiKey;

    /**
     * Validate JWT token and get user info
     */
    @Cacheable(value = "userInfo", key = "#token")
    public UserInfo validateToken(String token) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/auth/validate";
            return restClient.post()
                .uri(url)
                .header("Authorization", token)
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(UserInfo.class);
        } catch (Exception e) {
            log.error("Failed to validate token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get user info by user ID
     */
    @Cacheable(value = "userResponse", key = "#userId")
    public UserResponse getUserInfo(String userId) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/users/" + userId;
            return restClient.get()
                .uri(url)
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(UserResponse.class);
        } catch (Exception e) {
            log.error("Failed to get user info for userId {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Get batch user info
     */
    @Cacheable(value = "userBatchResponse", key = "#userIds.toString()")
    public List<UserResponse> getBatchUserInfo(List<String> userIds) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/users/batch?userIds=" + String.join(",", userIds);
            return restClient.get()
                .uri(url)
                .header("X-Internal-Api-Key", internalApiKey)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        } catch (Exception e) {
            log.error("Failed to get batch user info: {}", e.getMessage());
            throw e;
        }
    }
}
