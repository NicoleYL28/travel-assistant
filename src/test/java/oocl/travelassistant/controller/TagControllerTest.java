package oocl.travelassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.entity.Tag;
import oocl.travelassistant.repository.TagRepository;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*; // 新增导入以便使用 hasSize / hasItem
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TagControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;

    String token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        tagRepository.deleteAll();

        // 准备一些标签
        Tag t1 = new Tag(); t1.setName("火锅"); t1.setCategory("美食");
        Tag t2 = new Tag(); t2.setName("徒步"); t2.setCategory("运动");
        Tag t3 = new Tag(); t3.setName("海岛"); t3.setCategory("自然风光");
        tagRepository.save(t1); tagRepository.save(t2); tagRepository.save(t3);

        // 注册 & 登录获取 token
        String username = "tag_user";
        String password = "123456";
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"%s","password":"%s"}
                        """.formatted(username, password))
        ).andExpect(status().isOk());

        var res = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usernameOrEmail":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> json = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        token = (String) json.get("token");
        assertThat(token).isNotBlank();
    }

    @Test
    void should_return_unauthorized_without_token() throws Exception {
        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_get_all_tags_with_token() throws Exception {
        mockMvc.perform(get("/api/tags")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                // 返回应为长度为 3 的数组
                .andExpect(jsonPath("$", hasSize(3)))
                // 校验每个元素都包含 id / name / category 字段
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[2].id").isNumber())
                // 校验包含指定的名称集合（顺序不重要，可用 containsInAnyOrder 断言 names 列表）
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("火锅", "徒步", "海岛")))
                // 校验分类字段至少包含预期的几个分类
                .andExpect(jsonPath("$[*].category", hasItems("美食", "运动", "自然风光")));
    }
}
