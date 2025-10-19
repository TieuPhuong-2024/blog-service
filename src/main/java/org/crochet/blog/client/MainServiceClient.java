package org.crochet.blog.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.payload.UserInfo;
import org.crochet.blog.payload.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceClient {

    private final RestClient restClient;
    private final ObjectMapper om;

    @Value("${main-service.url}")
    private String mainServiceUrl;

    @Value("${main-service.internal-api-key}")
    private String internalApiKey;

    /**
     * Validate JWT token and get user info
     */
    public UserInfo validateToken(String token) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/auth/validate";
            var payload = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + token)
                    .header("X-Internal-Api-Key", internalApiKey)
                    .retrieve()
                    .body(String.class);
            var data = om.readTree(payload).get("data").toString();
            return om.readValue(data, UserInfo.class);
        } catch (Exception e) {
            log.error("Failed to validate token: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Get user info by user ID
     */
    public UserResponse getUserInfo(String userId) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/users/" + userId;
            var payload = restClient.get()
                    .uri(url)
                    .header("X-Internal-Api-Key", internalApiKey)
                    .retrieve()
                    .body(String.class);
            var data = om.readTree(payload).get("data").toString();
            return om.readValue(data, UserResponse.class);
        } catch (Exception e) {
            log.error("Failed to get user info for userId {}: {}", userId, e.getMessage());
        }
        return null;
    }

    /**
     * Get batch user info
     */
    public List<UserResponse> getBatchUserInfo(List<String> userIds) {
        try {
            String url = mainServiceUrl + "/api/v1/internal/users/batch?userIds=" + String.join(",", userIds);
            var payload = restClient.get()
                    .uri(url)
                    .header("X-Internal-Api-Key", internalApiKey)
                    .retrieve()
                    .body(String.class);
            var data = om.readTree(payload).get("data").toString();
            return om.readValue(data, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to get batch user info: {}", e.getMessage());
        }
        return null;
    }
}
