package oocl.travelassistant.controller;

import oocl.travelassistant.dto.TravelPlanDTO;
import oocl.travelassistant.service.TravelPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travel-plans")
public class TravelPlanController {

    @Autowired
    private TravelPlanService travelPlanService;

    @PostMapping
    public ResponseEntity<TravelPlanDTO> createTravelPlan(@RequestBody TravelPlanDTO travelPlanDTO, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        TravelPlanDTO createdPlan = travelPlanService.createTravelPlan(userId, travelPlanDTO);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping
    public ResponseEntity<List<TravelPlanDTO>> getUserTravelPlans(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<TravelPlanDTO> plans = travelPlanService.getTravelPlanDTOsByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelPlanDTO> getTravelPlan(@PathVariable Long id) {
        TravelPlanDTO plan = travelPlanService.getTravelPlanDTOById(id);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        boolean deleted = travelPlanService.deleteTravelPlanByIdAndUserId(id, userId);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}