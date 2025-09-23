package oocl.travelassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import oocl.travelassistant.dto.*;
import oocl.travelassistant.entity.User;
import oocl.travelassistant.repository.TravelPlanRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TravelPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        travelPlanRepository.deleteAll();
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

    private TravelPlanDTO createTravelPlanDTO() {
        TravelPlanDTO travelPlanDTO = new TravelPlanDTO();
        travelPlanDTO.setTitle("Test Travel Plan");
        travelPlanDTO.setOverview("A wonderful trip");
        travelPlanDTO.setDuration(3);
        travelPlanDTO.setTotalBudget(new BigDecimal("5000.00"));

        BudgetBreakdownDTO budgetBreakdown = new BudgetBreakdownDTO();
        budgetBreakdown.setAccommodation(new BigDecimal("2000.00"));
        budgetBreakdown.setFood(new BigDecimal("1500.00"));
        budgetBreakdown.setTransportation(new BigDecimal("1000.00"));
        budgetBreakdown.setActivities(new BigDecimal("500.00"));
        travelPlanDTO.setBudgetBreakdown(budgetBreakdown);

        DailyPlanDTO dailyPlan1 = new DailyPlanDTO();
        dailyPlan1.setDay(1);
        dailyPlan1.setDate("2025-09-24");  // 添加date字段
        dailyPlan1.setTheme("Arrival Day");
        dailyPlan1.setMorning("Arrive at airport");
        dailyPlan1.setAfternoon("Check in hotel");
        dailyPlan1.setEvening("City walk");

        MealsDTO meals = new MealsDTO();
        meals.setBreakfast("Hotel breakfast");
        meals.setLunch("Local restaurant");
        meals.setDinner("Fine dining");
        dailyPlan1.setMeals(meals);

        // 创建AccommodationDTO
        AccommodationDTO accommodation = new AccommodationDTO();
        accommodation.setName("Grand Hotel");
        accommodation.setAddress("123 Main St");
        accommodation.setRoomType("Deluxe Room");
        accommodation.setPrice(new BigDecimal("200.00"));
        accommodation.setBookingLink("https://booking.com/hotel123");
        dailyPlan1.setAccommodation(accommodation);

        TransportationDTO transportation = new TransportationDTO();
        transportation.setDetails("Flight AA123");
        transportation.setCost(new BigDecimal("500.00"));
        transportation.setBookingLink("https://airline.com/booking456");  // 添加bookingLink
        dailyPlan1.setTransportation(transportation);

        dailyPlan1.setDailyCost(new BigDecimal("800.00"));

        travelPlanDTO.setDailyPlan(List.of(dailyPlan1));
        travelPlanDTO.setTips(Arrays.asList("Bring sunscreen", "Learn basic phrases"));
        
        return travelPlanDTO;
    }

    @Test
    void should_create_travel_plan_successfully() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        
        String body = objectMapper.writeValueAsString(travelPlanDTO);
        
        mockMvc.perform(post("/api/travel-plans")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Travel Plan"))
                .andExpect(jsonPath("$.overview").value("A wonderful trip"))
                .andExpect(jsonPath("$.duration").value(3))
                .andExpect(jsonPath("$.totalBudget").value(5000.00));
    }

    @Test
    void should_get_user_travel_plans() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);
        
        mockMvc.perform(post("/api/travel-plans")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/travel-plans")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Test Travel Plan"));
    }

    @Test
    void should_get_travel_plan_by_id() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);
        
        String response = mockMvc.perform(post("/api/travel-plans")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
                
        Long travelPlanId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/travel-plans/" + travelPlanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(travelPlanId))
                .andExpect(jsonPath("$.title").value("Test Travel Plan"));
    }

    @Test
    void should_return_404_when_travel_plan_not_found() throws Exception {
        mockMvc.perform(get("/api/travel-plans/999")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_delete_travel_plan() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);
        
        String response = mockMvc.perform(post("/api/travel-plans")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
                
        long travelPlanId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/travel-plans/" + travelPlanId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void should_partially_update_travel_plan() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);

        String response = mockMvc.perform(post("/api/travel-plans")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long travelPlanId = objectMapper.readTree(response).get("id").asLong();

        String patchBody = """
            {
              "title": "Partially Updated Travel Plan"
            }
            """;

        mockMvc.perform(patch("/api/travel-plans/" + travelPlanId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Partially Updated Travel Plan"));
    }

    @Test
    void should_fully_update_travel_plan() throws Exception {
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);

        String response = mockMvc.perform(post("/api/travel-plans")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long travelPlanId = objectMapper.readTree(response).get("id").asLong();

        travelPlanDTO.setTitle("Fully Updated Travel Plan");
        String putBody = objectMapper.writeValueAsString(travelPlanDTO);

        mockMvc.perform(put("/api/travel-plans/" + travelPlanId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(putBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fully Updated Travel Plan"));
    }

    private String loginAndGetTokenFor() {
        try {
            User u = new User();
            u.setUsername("otheruser");
            u.setPasswordHash(passwordEncoder.encode("password"));
            userRepository.save(u);

            String loginBody = String.format("{%n  \"usernameOrEmail\": \"%s\",%n  \"password\": \"%s\"%n}%n", "otheruser", "password");
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

    @Test
    void should_return_401_when_no_auth() throws Exception {
        mockMvc.perform(get("/api/travel-plans"))
                .andExpect(status().isUnauthorized());

        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);
        mockMvc.perform(post("/api/travel-plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_400_for_malformed_json() throws Exception {
        String badJson = "{ invalid json }";

        mockMvc.perform(post("/api/travel-plans")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/travel-plans/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_404_when_updating_non_existent_travel_plan() throws Exception {
        String patchBody = "{ \"title\": \"Does not exist\" }";
        mockMvc.perform(put("/api/travel-plans/999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/travel-plans/999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return_404_when_updating_other_users_travel_plan() throws Exception {
        // 创建旅行计划（属于 testuser）
        TravelPlanDTO travelPlanDTO = createTravelPlanDTO();
        String body = objectMapper.writeValueAsString(travelPlanDTO);

        String response = mockMvc.perform(post("/api/travel-plans")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long travelPlanId = objectMapper.readTree(response).get("id").asLong();

        // 创建另一个用户并登录
        String otherToken = loginAndGetTokenFor();

        String updateBody = "{ \"title\": \"Hacked\" }";

        mockMvc.perform(put("/api/travel-plans/" + travelPlanId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/travel-plans/" + travelPlanId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }

}
