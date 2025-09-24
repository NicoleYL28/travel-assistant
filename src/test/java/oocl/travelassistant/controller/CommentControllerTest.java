package oocl.travelassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.CommentDTO;
import oocl.travelassistant.entity.Comment;
import oocl.travelassistant.entity.Post;
import oocl.travelassistant.entity.User;
import oocl.travelassistant.repository.CommentRepository;
import oocl.travelassistant.repository.PostRepository;
import oocl.travelassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private String jwtToken;
    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        jwtToken = loginAndGetToken();

        testPost = createAndSavePost("Test Post", "Some content");
    }

    @Test
    void should_create_top_level_comment_successfully() throws Exception {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setPostId(testPost.getId());
        commentDTO.setContent("This is a top-level comment.");

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a top-level comment."))
                .andExpect(jsonPath("$.parentId").doesNotExist());
    }

    @Test
    void should_create_reply_comment_successfully() throws Exception {
        Comment parentComment = createAndSaveComment("Parent comment", testPost.getId(), null);

        CommentDTO replyDTO = new CommentDTO();
        replyDTO.setPostId(testPost.getId());
        replyDTO.setContent("This is a reply.");
        replyDTO.setParentId(parentComment.getId());

        mockMvc.perform(post("/api/comments")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("This is a reply."))
                .andExpect(jsonPath("$.parentId").value(parentComment.getId()));
    }

    @Test
    void should_get_comments_for_post() throws Exception {
        createAndSaveComment("Comment 1", testPost.getId(), null);
        Comment parent = createAndSaveComment("Comment 2", testPost.getId(), null);
        createAndSaveComment("Reply to comment 2", testPost.getId(), parent.getId()); // This is a reply, should not be fetched

        mockMvc.perform(get("/api/comments/posts/" + testPost.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Comment 2")); // Sorted by creation time desc
    }

    @Test
    void should_get_replies_for_comment() throws Exception {
        Comment parentComment = createAndSaveComment("Parent Comment", testPost.getId(), null);
        createAndSaveComment("Reply 1", testPost.getId(), parentComment.getId());
        createAndSaveComment("Reply 2", testPost.getId(), parentComment.getId());

        mockMvc.perform(get("/api/comments/" + parentComment.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Reply 2"));
    }

    private String loginAndGetToken() {
        try {
            String loginBody = String.format("""
                {
                  "usernameOrEmail": "%s",
                  "password": "password"
                }
                """, testUser.getUsername());

            String response = mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readTree(response).get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get JWT token", e);
        }
    }

    private Post createAndSavePost(String title, String content) {
        Post post = new Post();
        post.setUser(testUser);
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    private Comment createAndSaveComment(String content, Long postId, Long parentId) {
        Comment comment = new Comment();
        comment.setUser(testUser);
        comment.setPostId(postId);
        comment.setContent(content);
        comment.setParentId(parentId);
        return commentRepository.save(comment);
    }
}
