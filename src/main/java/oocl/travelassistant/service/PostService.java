package oocl.travelassistant.service;

import oocl.travelassistant.dto.PostDTO;
import oocl.travelassistant.entity.Post;
import oocl.travelassistant.exception.PostNotFoundException;
import oocl.travelassistant.exception.UnauthorizedAccessException;
import oocl.travelassistant.repository.PostRepository;
import oocl.travelassistant.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private  final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post createPost(Long userId, PostDTO postDTO) {
        Post post = new Post();
        post.setUser(userRepository.findById(userId).get());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        return postRepository.save(post);
    }

    public Post updatePost(Long userId, Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            throw new PostNotFoundException("Post not found");
        }
        if(!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Forbidden");
        }
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        return postRepository.save(post);
    }

    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            throw new PostNotFoundException("Post not found");
        }
        if(!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Forbidden");
        }
        postRepository.delete(post);
    }

    public Page<Post> getPosts(Pageable pageable) {
        return postRepository.findAllByOrderByUpdatedAtDesc(pageable);
    }

    public Page<Post> getPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId, pageable);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }
}
