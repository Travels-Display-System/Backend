package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public User addUser(String username, String password) throws JsonProcessingException {

        return addUser(username,password,1,0);
    }

    public User addUser(String username, String password, Integer enabled, Integer iseditor) throws JsonProcessingException {

        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User";

        String postContent="{\"username\":\""+username+"\", \"password\":\""+password+"\", \"enabled\":"+enabled.toString()+",\"iseditor\":"+iseditor.toString()+"}";

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , User.class);
    }

    public User getUser(String username,Long id) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User/";
        if(username!=null){
            Url+="?User.username="+username;
            return mapper.readValue(DatabaseUtils.sendGetRequest(Url) , User.class);
        }
        if(id!=null){
            Url+=id;
            return mapper.readValue(DatabaseUtils.sendGetRequest(Url) , User.class);
        }
        return null;
    }

    public List<User> getUserAll() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User/";
        String result=DatabaseUtils.sendGetRequest(Url);
        List<User> users = mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<User>>(){/**/});
        return users;
    }

    

}
