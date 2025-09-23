package oocl.travelassistant.service;

import oocl.travelassistant.dto.TagDTO;
import oocl.travelassistant.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getCategory()))
                .collect(Collectors.toList());
    }
}
