package oocl.travelassistant.controller;

import oocl.travelassistant.dto.TagDTO;
import oocl.travelassistant.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }
    @GetMapping
    public List<TagDTO> getAllTags() {
        return tagService.getAllTags();
    }
}
