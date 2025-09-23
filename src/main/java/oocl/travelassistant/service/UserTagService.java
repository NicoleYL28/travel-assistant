package oocl.travelassistant.service;

import oocl.travelassistant.dto.TagDTO;
import oocl.travelassistant.entity.Tag;
import oocl.travelassistant.entity.UserTag;
import oocl.travelassistant.repository.TagRepository;
import oocl.travelassistant.repository.UserTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserTagService {
    private final UserTagRepository userTagRepository;
    private final TagRepository tagRepository;

    public UserTagService(UserTagRepository userTagRepository, TagRepository tagRepository) {
        this.userTagRepository = userTagRepository;
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> assignTagsToUser(Long userId, List<Long> tagIds) {
        //筛选有效id
        List<Long> validTagIds = tagRepository.findAllById(tagIds)
                .stream()
                .map(Tag::getId)
                .toList();
        // 先删除旧的
        userTagRepository.deleteAll(userTagRepository.findByUserId(userId));
        // 插入新的
        for (Long tagId : validTagIds) {
            userTagRepository.save(new UserTag(userId, tagId));
        }
        // 返回用户最新的标签
        return getTagsForUser(userId);
    }

    public List<TagDTO> getTagsForUser(Long userId) {
        List<UserTag> userTags = userTagRepository.findByUserId(userId);
        return userTags.stream()
                .map(userTag -> tagRepository.findById(userTag.getTagId()).orElse(null))
                .filter(tag -> tag != null)
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getCategory()))
                .collect(Collectors.toList());
    }


}
