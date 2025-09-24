package oocl.travelassistant.repository;

import oocl.travelassistant.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    Page<Post> findAllByUser_IdOrderByUpdatedAtDesc(Long userId, Pageable pageable);
}
