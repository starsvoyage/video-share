package edu.arizona.videoshare.repository;

import edu.arizona.videoshare.model.entity.Comment;
import edu.arizona.videoshare.model.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByVideoIdAndParentIsNullOrderByCreatedAtDesc(Long videoId);

    List<Comment> findByParent_IdOrderByCreatedAtAsc(Long parentId);

    int countByParent_IdAndStatus(Long parentId, CommentStatus status);
}