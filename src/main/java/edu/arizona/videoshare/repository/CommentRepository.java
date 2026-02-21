package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoIdAndParentIsNullOrderByCreatedAtDesc(Long videoId);

    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
}
