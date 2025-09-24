package oocl.travelassistant.service;

import lombok.RequiredArgsConstructor;
import oocl.travelassistant.dto.CommentDTO;
import oocl.travelassistant.entity.Comment;
import oocl.travelassistant.exception.CommentNotFoundException;
import oocl.travelassistant.exception.PostNotFoundException;
import oocl.travelassistant.exception.UserNotFoundException;
import oocl.travelassistant.repository.CommentRepository;
import oocl.travelassistant.repository.PostRepository;
import oocl.travelassistant.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Comment createComment(Long userId,CommentDTO commentDTO) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPostId(post.getId());
        comment.setContent(commentDTO.getContent());

        if (commentDTO.getParentId() != null) {
            var parentComment = commentRepository.findById(commentDTO.getParentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment not found"));
            comment.setParentId(parentComment.getId());
        }

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found");
        }
        return commentRepository.findTopLevelCommentsByPostId(postId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getRepliesByCommentId(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Comment not found");
        }
        return commentRepository.findByParentIdOrderByCreatedAtDesc(commentId);
    }
}
