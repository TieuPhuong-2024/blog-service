package org.crochet.blog.repository;

import org.crochet.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, String>, JpaSpecificationExecutor<Post> {

    @Query("""
            SELECT
                p
            FROM
                Post p
            LEFT JOIN FETCH p.files
            WHERE
                p.id = :id
            """)
    Optional<Post> getDetail(@Param("id") String id);

    @Query("""
            SELECT
                p.id
            FROM
                Post p
            ORDER BY
                p.createdAt DESC
            """)
    List<String> getPostIds(Pageable pageable);

    // Find posts marked as home
    List<Post> findByShowOnHomeTrueOrderByCreatedAtDesc(Pageable pageable);

    // Find all posts with pagination
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
