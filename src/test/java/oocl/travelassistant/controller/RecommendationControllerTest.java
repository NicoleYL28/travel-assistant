package oocl.travelassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.AssignTagsRequestDto;
import oocl.travelassistant.entity.Recommendation;
import oocl.travelassistant.entity.Tag;
import oocl.travelassistant.repository.RecommendationRepository;
import oocl.travelassistant.repository.TagRepository;
import oocl.travelassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long tagId1;
    private Long tagId2;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up repositories
        recommendationRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        // Create test tags
        Tag tag1 = new Tag();
        tag1.setName("餐厅");
        tag1.setCategory("美食");
        tagRepository.save(tag1);
        tagId1 = tag1.getId();

        Tag tag2 = new Tag();
        tag2.setName("博物馆");
        tag2.setCategory("文化");
        tagRepository.save(tag2);
        tagId2 = tag2.getId();

        // Create test recommendations
        Recommendation rec1 = new Recommendation(tagId1, "北京烤鸭", "http://example.com/duck", "4.5",
                "http://example.com/duck.jpg", "著名的北京特色美食");
        Recommendation rec2 = new Recommendation(tagId1, "上海小笼包", "http://example.com/xiaolongbao", "4.3",
                "http://example.com/xiaolongbao.jpg", "上海传统点心");
        Recommendation rec3 = new Recommendation(tagId2, "故宫博物院", "http://example.com/palace", "5.0",
                "http://example.com/palace.jpg", "中国明清两代的皇家宫殿");

        recommendationRepository.saveAll(Arrays.asList(rec1, rec2, rec3));

        // Register a test user
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"rec_test_user\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        // Login to get auth token
        var result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usernameOrEmail\":\"rec_test_user\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> json = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        token = (String) json.get("token");
        assertThat(token).isNotBlank();
    }

    @Test
    void should_return_unauthorized_without_token() throws Exception {
        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_all_recommendations_when_no_tag_ids_provided() throws Exception {
        mockMvc.perform(get("/api/recommendations")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("北京烤鸭", "上海小笼包", "故宫博物院")));
    }

    @Test
    void should_return_filtered_recommendations_by_tag_id() throws Exception {
        AssignTagsRequestDto requestDto = new AssignTagsRequestDto();
        requestDto.setTagIds(List.of(tagId1));

        mockMvc.perform(get("/api/recommendations")
                .header("Authorization", "Bearer " + token)
                .param("tagIds", tagId1.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("北京烤鸭", "上海小笼包")))
                .andExpect(jsonPath("$[*].tagId", everyItem(is(tagId1.intValue()))));
    }

    @Test
    void should_return_empty_list_when_no_recommendations_match_tag_id() throws Exception {
        Long nonExistentTagId = 999L;

        mockMvc.perform(get("/api/recommendations")
                .header("Authorization", "Bearer " + token)
                .param("tagIds", nonExistentTagId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
