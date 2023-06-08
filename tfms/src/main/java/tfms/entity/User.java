package tfms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("user_status_id")
    public String user_status_id;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser_status_id() {
        return user_status_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUser_status_id(String user_status_id) {
        this.user_status_id = user_status_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_status_id='" + user_status_id + '\'' +
                '}';
    }
}
