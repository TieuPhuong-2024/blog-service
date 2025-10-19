package org.crochet.blog.util;

import org.crochet.blog.payload.response.FileResponse;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ImageUtil {
    /**
     * Sort files by the last modified date and assign order
     *
     * @param <T>   Type of file extending FileResponse
     * @param files Collection of files to sort
     * @return Sorted list of files with order assigned, or null if input is empty
     */
    public static <T extends FileResponse> List<T> sortFiles(Collection<T> files) {
        if (ObjectUtil.isEmpty(files)) {
            return List.of();
        }

        List<T> sortedFiles = files.stream()
                .sorted(Comparator.comparing(FileResponse::getLastModified,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        int order = 0;
        for (T file : sortedFiles) {
            file.setOrder(order++);
        }

        return sortedFiles;
    }
}
