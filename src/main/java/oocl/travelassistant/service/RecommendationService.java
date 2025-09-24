package oocl.travelassistant.service;

import oocl.travelassistant.dto.AssignTagsRequestDto;
import oocl.travelassistant.entity.Recommendation;
import oocl.travelassistant.repository.RecommendationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public List<Recommendation> getRecommendationsByTagId(List<Long> tagIds) {
        return recommendationRepository.findByTagIdIn(tagIds);
    }

    public List<Recommendation> getAllRecommendations() {
        return recommendationRepository.findAll();
    }
}
