package oocl.travelassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.entity.Tag;
import oocl.travelassistant.repository.TagRepository;
import oocl.travelassistant.repository.UserRepository;
import oocl.travelassistant.repository.UserTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserTagControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserTagRepository userTagRepository;
    @Autowired
    ObjectMapper objectMapper;

    String token;
    Long tagId1;
    Long tagId2;
    Long tagId3;

    @BeforeEach
    void setup() throws Exception {
        userTagRepository.deleteAll();
        userRepository.deleteAll();
        tagRepository.deleteAll();

        Tag t1 = new Tag(); t1.setName("火锅"); t1.setCategory("美食"); tagRepository.save(t1); tagId1 = t1.getId();
        Tag t2 = new Tag(); t2.setName("徒步"); t2.setCategory("运动"); tagRepository.save(t2); tagId2 = t2.getId();
        Tag t3 = new Tag(); t3.setName("海岛"); t3.setCategory("自然风光"); tagRepository.save(t3); tagId3 = t3.getId();

        // register user (使用普通字符串避免文本块语法错误)
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ut_user\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        var res = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernameOrEmail\":\"ut_user\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> json = objectMapper.readValue(res.getResponse().getContentAsString(), Map.class);
        token = (String) json.get("token");
        assertThat(token).isNotBlank();
    }

    @Test
    void should_return_unauthorized_without_token_when_assign() throws Exception {
        mockMvc.perform(put("/api/user/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tagIds\":[1,2]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_assign_and_get_tags() throws Exception {
        // assign two tags
        mockMvc.perform(put("/api/user/tags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("tagIds", List.of(tagId1, tagId2)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());

        // get tags
        mockMvc.perform(get("/api/user/tags")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber());
    }

    @Test
    void should_overwrite_previous_tags_on_reassign() throws Exception {
        // first assign tag1, tag2
        mockMvc.perform(put("/api/user/tags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("tagIds", List.of(tagId1, tagId2)))))
                .andExpect(status().isOk());

        // reassign tag3 only
        mockMvc.perform(put("/api/user/tags")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("tagIds", List.of(tagId3)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(tagId3));

        // confirm only one tag now
        mockMvc.perform(get("/api/user/tags")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" ).value(1))
                .andExpect(jsonPath("$[0].id").value(tagId3));
    }
}
