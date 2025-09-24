package oocl.travelassistant.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.PostDTO;
import oocl.travelassistant.entity.Post;
import oocl.travelassistant.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPasswordHash(passwordEncoder.encode("password"));
        userRepository.save(testUser);

        jwtToken = loginAndGetToken();
    }
    private String loginAndGetToken() {
        try {
            String loginBody = """
                {
                  "usernameOrEmail": "testuser",
                  "password": "password"
                }
                """;

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

    @Test
    void should_create_post_successfully() throws Exception {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle("My First Post");
        postDTO.setContent("This is the content of my first post.");

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My First Post"))
                .andExpect(jsonPath("$.content").value("This is the content of my first post."));
    }

    @Test
    void should_update_post_successfully() throws Exception {
        Post post = createAndSavePost("Original Title", "Original content");

        PostDTO updatedDto = new PostDTO();
        updatedDto.setTitle("Updated Title");
        updatedDto.setContent("Updated content");

        mockMvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    void should_delete_post_successfully() throws Exception {
        Post post = createAndSavePost("To Be Deleted", "Some content");

        mockMvc.perform(delete("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_get_all_posts_paginated() throws Exception {
        createAndSavePost("Post 1", "Content 1");
        createAndSavePost("Post 2", "Content 2");

        mockMvc.perform(get("/api/posts?page=1&size=10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void should_get_my_posts_paginated() throws Exception {
        createAndSavePost("My Post 1", "My Content 1");
        createAndSavePost("My Post 2", "My Content 2");

        // Create a post for another user that should not appear in the result
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPasswordHash(passwordEncoder.encode("password"));
        userRepository.save(otherUser);
        Post otherPost = new Post();
        otherPost.setUser(otherUser);
        otherPost.setTitle("Other's Post");
        otherPost.setContent("Other's content");
        postRepository.save(otherPost);

        mockMvc.perform(get("/api/posts/mine")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].title").value("My Post 2")) // Sorted by update time desc
                .andExpect(jsonPath("$.content[1].title").value("My Post 1"));
    }

    @Test
    void should_get_post_by_id_successfully() throws Exception {
        Post post = createAndSavePost("Test Post Title", "Test Post Content");

        mockMvc.perform(get("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("Test Post Title"))
                .andExpect(jsonPath("$.content").value("Test Post Content"));
    }

    @Test
    void should_return_404_when_getting_non_existent_post() throws Exception {
        mockMvc.perform(get("/api/posts/99999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_403_when_updating_other_users_post() throws Exception {
        // Create a post for the main test user
        Post post = createAndSavePost("Original Title", "Original content");

        // Create and log in as another user
        String otherToken = loginAndGetTokenFor("otheruser2", "password");

        PostDTO updatedDto = new PostDTO();
        updatedDto.setTitle("Hacked Title");
        updatedDto.setContent("Hacked content");

        // Attempt to update the original user's post with the other user's token
        mockMvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void should_return_401_when_no_auth() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    private Post createAndSavePost(String title, String content) {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        Post post = new Post();
        post.setUser(user);
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    private String loginAndGetTokenFor(String username, String password) {
        try {
            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(passwordEncoder.encode(password));
            userRepository.save(u);

            String loginBody = String.format("""
                {
                  "usernameOrEmail": "%s",
                  "password": "%s"
                }
                """, username, password);
            String response = mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            return objectMapper.readTree(response).get("token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create/login user", e);
        }
    }
}
