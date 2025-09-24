package oocl.travelassistant.controller;

import lombok.RequiredArgsConstructor;
import oocl.travelassistant.dto.CommentDTO;
import oocl.travelassistant.entity.Comment;
import oocl.travelassistant.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDTO commentDTO,
                                                 Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Comment createdComment = commentService.createComment(userId,commentDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<List<Comment>> getRepliesByComment(@PathVariable Long commentId) {
        List<Comment> replies = commentService.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }
}

