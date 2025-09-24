package oocl.travelassistant.service;

import oocl.travelassistant.dto.AssignTagsRequestDto;
import oocl.travelassistant.entity.Recommendation;
import oocl.travelassistant.repository.RecommendationRepository;
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
        if (tagIds == null || tagIds.isEmpty()) {
            return recommendationRepository.findAll();
        }
        return recommendationRepository.findByTagIdIn(tagIds);
    }
}
