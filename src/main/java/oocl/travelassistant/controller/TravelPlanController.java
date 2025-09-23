package oocl.travelassistant.controller;

import lombok.RequiredArgsConstructor;
import oocl.travelassistant.dto.TravelPlanDTO;
import oocl.travelassistant.entity.TravelPlan;
import oocl.travelassistant.service.TravelPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-plans")
@RequiredArgsConstructor
public class TravelPlanController {

    private final TravelPlanService travelPlanService;

    @PostMapping
    public ResponseEntity<TravelPlan> createTravelPlan(@RequestBody TravelPlanDTO travelPlanDTO, Authentication authentication) {
        // authentication.getName() 返回的是用户ID，不是用户名
        Long userId = Long.valueOf(authentication.getName());
        TravelPlan createdPlan = travelPlanService.createTravelPlan(userId, travelPlanDTO);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping
    public ResponseEntity<List<TravelPlan>> getUserTravelPlans(Authentication authentication) {
        // authentication.getName() 返回的是用户ID，不是用户名
        Long userId = Long.valueOf(authentication.getName());
        List<TravelPlan> plans = travelPlanService.getTravelPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPlan> getTravelPlan(@PathVariable Long id) {
        TravelPlan plan = travelPlanService.getTravelPlanById(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable Long id, Authentication authentication) {
        // authentication.getName() 返回的是用户ID，不是用户名
        Long userId = Long.valueOf(authentication.getName());
        boolean deleted = travelPlanService.deleteTravelPlanByIdAndUserId(id, userId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}