package tfms.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tfms.entity.Task;
import tfms.entity.User;

public class JSONParserToUser {
    public static User parse(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(jsonString, User.class);
        return user;
    }
}
