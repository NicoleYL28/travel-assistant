package oocl.travelassistant.controller;

import oocl.travelassistant.dto.AssignTagsRequestDto;
import oocl.travelassistant.entity.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import oocl.travelassistant.service.RecommendationService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<Recommendation>> getRecommendationsByTagId(AssignTagsRequestDto assignTagsRequestDto) {
        if(assignTagsRequestDto == null || assignTagsRequestDto.getTagIds() == null || assignTagsRequestDto.getTagIds().isEmpty()) {
            List<Recommendation> recommendations = recommendationService.getAllRecommendations();
            return ResponseEntity.ok(recommendations);
        }
        List<Recommendation> recommendations = recommendationService.getRecommendationsByTagId(assignTagsRequestDto.getTagIds());
        return ResponseEntity.ok(recommendations);
    }


}
