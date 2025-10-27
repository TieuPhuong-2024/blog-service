package org.crochet.blog.util;

import org.crochet.blog.payload.response.ResponseData;
import org.springframework.http.HttpStatus;

/**
 * Utility class for creating standardized ResponseData objects
 */
public class ResponseUtil {

    private static final String SUCCESS = "Success";

    private ResponseUtil() {
        // Prevent instantiation
    }

    /**
     * Create success response with data
     *
     * @param data The data to return
     * @param <T>  The type of data
     * @return ResponseData object
     */
    public static <T> ResponseData<T> success(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(SUCCESS)
                .data(data)
                .build();
    }

    /**
     * Create success response with data and custom message
     *
     * @param data    The data to return
     * @param message The success message
     * @param <T>     The type of data
     * @return ResponseData object
     */
    public static <T> ResponseData<T> success(T data, String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create success response without data
     *
     * @param message The success message
     * @param <T>     The type of data
     * @return ResponseData object
     */
    public static <T> ResponseData<T> success(String message) {
        return ResponseData.<T>builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    /**
     * Create error response
     *
     * @param status  HTTP status
     * @param message Error message
     * @param <T>     The type of data
     * @return ResponseData object
     */
    public static <T> ResponseData<T> error(HttpStatus status, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .code(status.value())
                .message(message)
                .build();
    }
}
