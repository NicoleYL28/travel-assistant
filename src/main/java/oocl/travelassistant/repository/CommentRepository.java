package oocl.travelassistant.repository;

import oocl.travelassistant.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.parentId IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findTopLevelCommentsByPostId(Long postId);

    List<Comment> findByParentIdOrderByCreatedAtDesc(Long parentId);
}
