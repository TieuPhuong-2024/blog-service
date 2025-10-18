package org.crochet.blog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.blog.client.MainServiceClient;
import org.crochet.blog.payload.UserInfo;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

/**
 * JWT Authentication Filter for Blog Service
 * Validates JWT tokens by calling Main Service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MainServiceClient mainServiceClient;

    /**
     * Performs the filtering logic for the authentication process.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Get jwtToken from Authorization header
            String jwtToken = getJwtFromAuthorizationHeader(request);

            // Check if the JWT exists
            if (hasText(jwtToken)) {
                // Validate token via Main Service
                UserInfo userInfo = mainServiceClient.validateToken(jwtToken);

                if (userInfo != null) {
                    // Convert roles to authorities
                    List<SimpleGrantedAuthority> authorities = userInfo.getRoles()
                            .stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userInfo,
                            null,
                            authorities
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context for path: {}", request.getRequestURI(), ex);
            // Continue processing even if token validation fails
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromAuthorizationHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
