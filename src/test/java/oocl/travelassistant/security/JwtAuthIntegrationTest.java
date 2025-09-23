package oocl.travelassistant.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class JwtAuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    String email = "jwt_test@example.com";
    String username = "jwt_user";
    String password = "123456";
    String token;

    @BeforeEach
    void setup() throws Exception {
        // 注册（唯一约束已存在可忽略异常）
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          \"email\":\"%s\",
                          \"username\":\"%s\",
                          \"password\":\"%s\"
                        }
                        """.formatted(email, username, password)));

        // 登录获取 token
        var res = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"usernameOrEmail\":\"%s\",
                                  \"password\":\"%s\"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> json = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        token = (String) json.get("token");
    }

    @AfterEach
    void cleanup() {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
    }

    @Test
    void should_fail_without_token() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_pass_with_valid_token() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void should_fail_with_fake_token() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer FAKE." + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_fail_when_signature_tampered() throws Exception {
        String bad = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + bad))
                .andExpect(status().isUnauthorized());
    }
}
