package tfms.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tfms.entity.Task;

public class JSONParser {
    public static Task parse(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Task task = mapper.readValue(jsonString, Task.class);
        return task;
    }
}
