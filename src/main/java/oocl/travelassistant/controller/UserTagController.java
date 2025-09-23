package oocl.travelassistant.controller;

import oocl.travelassistant.dto.AssignTagsRequestDto;
import oocl.travelassistant.dto.TagDTO;
import oocl.travelassistant.service.UserTagService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/tags")
public class UserTagController {

    private final UserTagService userTagService;

    public UserTagController(UserTagService userTagService) {
        this.userTagService = userTagService;
    }

    // 给当前用户设置标签（覆盖更新）
    @PutMapping
    public List<TagDTO> assignTags(@RequestBody AssignTagsRequestDto assignTagsRequestDto, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userTagService.assignTagsToUser(userId, assignTagsRequestDto.getTagIds());
    }

    // 获取当前用户的标签
    @GetMapping
    public List<TagDTO> getUserTags(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return userTagService.getTagsForUser(userId);
    }
}
