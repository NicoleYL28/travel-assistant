package oocl.travelassistant.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_tags")
@IdClass(UserTagId.class)
public class UserTag {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    public UserTag() {}
    public UserTag(Long userId, Long tagId) {
        this.userId = userId;
        this.tagId = tagId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }
    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}

