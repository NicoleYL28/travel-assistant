package oocl.travelassistant.controller;

import oocl.travelassistant.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void should_register_success_with_username_only() throws Exception {
        String body = """
                {
                  "username": "jack",
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("jack"))
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void should_register_success_with_email_only() throws Exception {
        String body = """
                {
                  "email": "jack@example.com",
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("jack@example.com"))
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void should_return_400_when_register_without_username_and_email() throws Exception {
        String body = """
                {
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注册必须提供用户名或邮箱之一"));
    }

    @Test
    void should_return_409_when_register_with_existing_username() throws Exception {
        User existing = new User();
        existing.setUsername("jack");
        existing.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(existing);

        String body = """
                {
                  "username": "jack",
                  "password": "abcdef"
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void should_return_409_when_register_with_existing_email() throws Exception {
        User existing = new User();
        existing.setEmail("jack@example.com");
        existing.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(existing);

        String body = """
                {
                  "email": "jack@example.com",
                  "password": "abcdef"
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("邮箱已存在"));
    }

    @Test
    void should_return_400_when_register_with_empty_password() throws Exception {
        String body = """
                {
                  "username": "jack",
                  "password": ""
                }
                """;
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));
    }

    @Test
    void should_login_success_with_username_and_token() throws Exception {
        User user = new User();
        user.setUsername("jack");
        user.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(user);

        String body = """
                {
                  "usernameOrEmail": "jack",
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jack"))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void should_login_success_with_email_and_token() throws Exception {
        User user = new User();
        user.setEmail("jack@example.com");
        user.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(user);

        String body = """
                {
                  "usernameOrEmail": "jack@example.com",
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jack@example.com"))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void should_return_404_when_login_with_non_existing_account() throws Exception {
        String body = """
                {
                  "usernameOrEmail": "notfound",
                  "password": "123456"
                }
                """;
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void should_return_401_when_login_with_wrong_password() throws Exception {
        User user = new User();
        user.setUsername("jack");
        user.setPasswordHash(passwordEncoder.encode("123456"));
        userRepository.save(user);

        String body = """
                {
                  "usernameOrEmail": "jack",
                  "password": "wrongpass"
                }
                """;
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));
    }
}

