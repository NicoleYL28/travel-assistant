package oocl.travelassistant.repository;

import oocl.travelassistant.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByTagIdIn(List<Long> tagIds);
}
