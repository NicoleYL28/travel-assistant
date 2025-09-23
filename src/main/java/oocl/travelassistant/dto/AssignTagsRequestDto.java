package oocl.travelassistant.dto;

import java.util.List;

public class AssignTagsRequestDto {
    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return tagIds;
    }
    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
