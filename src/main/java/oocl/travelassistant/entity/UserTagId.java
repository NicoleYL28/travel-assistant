package oocl.travelassistant.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserTagId implements Serializable {
    private Long userId;
    private Long tagId;

    public UserTagId() {}
    public UserTagId(Long userId, Long tagId) {
        this.userId = userId;
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTagId)) return false;
        UserTagId that = (UserTagId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tagId);
    }
}
