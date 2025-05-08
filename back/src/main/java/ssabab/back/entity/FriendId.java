package ssabab.back.entity;

import java.io.Serializable;
import java.util.Objects;

public class FriendId implements Serializable {
    private Integer user;
    private Integer friend;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendId that = (FriendId) o;
        return Objects.equals(user, that.user) && Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, friend);
    }
}