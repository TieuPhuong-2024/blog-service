package org.crochet.blog.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for common Object operations.
 */
public final class ObjectUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ObjectUtil() {
    }

    /**
     * Checks if an object is null.
     *
     * @param obj the object to check
     * @return true if the object is null, false otherwise
     */
    public static boolean isNull(final Object obj) {
        return obj == null;
    }

    /**
     * Checks if an object is not null.
     *
     * @param obj the object to check
     * @return true if the object is not null, false otherwise
     */
    public static boolean isNotNull(final Object obj) {
        return obj != null;
    }

    /**
     * Checks if an object, collection, map, or array is empty or null.
     *
     * @param obj the object to check
     * @return true if the object is empty or null, false otherwise
     */
    public static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }

        if (obj instanceof String) {
            return ((String) obj).isEmpty();
        }

        if (obj instanceof Optional) {
            return ((Optional<?>) obj).isEmpty();
        }

        return false;
    }

    /**
     * Checks if an object, collection, map, or array is not empty and not null.
     *
     * @param obj the object to check
     * @return true if the object is not empty and not null, false otherwise
     */
    public static boolean isNotEmpty(final Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Checks if a string has text (not null, not empty, not only whitespace).
     *
     * @param str the string to check
     * @return true if the string has text, false otherwise
     */
    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
