package org.crochet.blog.util;

import org.crochet.blog.payload.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for common security operations.
 */
public final class SecurityUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SecurityUtil() {
    }

    /**
     * Get current user ID from a security context.
     *
     * @return current user ID
     * @throws RuntimeException if the user is not authenticated or principal is not UserInfo
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserInfo userInfo) {
            return userInfo.getId();
        }
        throw new RuntimeException("User not authenticated");
    }

    /**
     * Get current user info from a security context.
     *
     * @return current UserInfo
     * @throws RuntimeException if the user is not authenticated or principal is not UserInfo
     */
    public static UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserInfo userInfo) {
            return userInfo;
        }
        throw new RuntimeException("User not authenticated");
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getPrincipal() instanceof UserInfo;
    }
}