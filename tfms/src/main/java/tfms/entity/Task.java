package tfms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;

public class Task {


    @JsonProperty("id")
    public String id;
    @JsonProperty("external_id")
    public String external_id;
    @JsonProperty("priority")
    public int priority;
    @JsonProperty("appointed_user")
    public String appointed_user;
    @JsonProperty("register_time")
    public String register_time;
    @JsonProperty("task_status_id")
    public String task_status_id;
    @JsonProperty("task_type")
    public String task_type;
    @JsonProperty("body")
    public String body;
    @JsonProperty("final_user")
    public String final_user;

    public String getId() {
        return id;
    }

    public String getExternal_id() {
        return external_id;
    }

    public int getPriority() {
        return priority;
    }

    public String getAppointed_user() {
        return appointed_user;
    }

    public String getRegister_time() {
        return register_time;
    }

    public String getTask_status_id() {
        return task_status_id;
    }

    public String getTask_type() {
        return task_type;
    }

    public String getBody() {
        return body;
    }

    public String getFinal_user() {
        return final_user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setAppointed_user(String appointed_user) {
        this.appointed_user = appointed_user;
    }

    public void setRegister_time(String register_time) {
        this.register_time = register_time;
    }

    public void setTask_status_id(String task_status_id) {
        this.task_status_id = task_status_id;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFinal_user(String body) {
        this.final_user = final_user;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", external_id='" + external_id + '\'' +
                ", priority=" + priority +
                ", appointed_user='" + appointed_user + '\'' +
                ", register_time='" + register_time + '\'' +
                ", task_status_id='" + task_status_id + '\'' +
                ", task_type='" + task_type + '\'' +
                ", body='" + body + '\'' +
                ", final_user='" + final_user + '\'' +
                '}';
    }

    public String toJSONString() {
        return "{\n" +
                "\t\"id\": \"" + id + "\",\n" +
                "\t\"external_id\": \"" + external_id + "\",\n" +
                "\t\"priority\": \"" + priority + "\",\n" +
                "\t\"appointed_user\": \"" + appointed_user + "\",\n" +
                "\t\"register_time\": \"" + register_time + "\",\n" +
                "\t\"task_status_id\": \"" + task_status_id + "\",\n" +
                "\t\"task_type\": \"" + task_type + "\",\n" +
                "\t\"body\": \"" + body + "\",\n" +
                "\t\"final_user\": \"" + final_user + "\"\n" +
                "}";
    }
}
