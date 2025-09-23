package oocl.travelassistant.repository;

import oocl.travelassistant.entity.UserTag;
import oocl.travelassistant.entity.UserTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, UserTagId> {
    List<UserTag> findByUserId(Long userId);
}