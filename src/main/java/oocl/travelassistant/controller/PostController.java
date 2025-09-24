package oocl.travelassistant.controller;

import lombok.RequiredArgsConstructor;
import oocl.travelassistant.dto.PostDTO;
import oocl.travelassistant.entity.Post;
import oocl.travelassistant.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController  {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDTO postDTO, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Post post = postService.createPost(userId, postDTO);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,@RequestBody PostDTO postDTO, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        Post post = postService.updatePost(userId, id,postDTO);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<Post>> getMyPosts(Authentication authentication,
                                                 @RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size) {
        Long userId = Long.valueOf(authentication.getName());
        Pageable pageable = PageRequest.of(page -1, size);
        Page<Post> posts = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Post> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    //通过post id获取post
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        postService.deletePost(userId, id);
        return ResponseEntity.noContent().build();
    }
}
