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

        //username不能重复
        User user=getUser(username,null);
        if(user!=null){
            System.out.print("User has already exist");
            return user;
        }

        String postContent="{\"username\":\""+username+"\", \"password\":\""+password+"\", \"enabled\":"+enabled.toString()+",\"iseditor\":"+iseditor.toString()+"}";

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , User.class);
    }

    public User getUser(String username,Long id) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User/";
        if(username!=null){
            Url+="?User.username="+username;
            String result = DatabaseUtils.sendGetRequest(Url);
//            System.out.print(result);
//            System.out.print(result.substring(result.indexOf("["),result.lastIndexOf("]")+1));
            List<User> users=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1) , new TypeReference<List<User>>(){/**/});
            return users.get(0);
        }
        if(id!=null){
            Url+=id;
            String result = DatabaseUtils.sendGetRequest(Url);
            return mapper.readValue(result, User.class);
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

    public void modifyUser(User user) throws JsonProcessingException {
        Long id=0L;
        User oldUser=new User();
        id=user.getId();
        if(id==null){
            System.out.print("id is empty");
            oldUser=getUser(user.getUsername(),null);
            if(oldUser==null){
                System.out.print("User does not exist");
                return;
            }
            id=oldUser.getId();
            System.out.print(id);
        }
        else{
            oldUser=getUser(null,id);
        }
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User/"+id;
        String username=user.getUsername();
        String password=user.getPassword();
        Integer enabled=user.getEnabled();
        Integer iseditor=user.getIseditor();
        if(username!=null && user.getId()!=null){
            if(getUser(username,null)==null){
                System.out.print("username has already been used.");
                return;
            }
        }
        if(username==null){ username=oldUser.getUsername(); }
        if(password==null){ password=oldUser.getPassword(); }
        if(enabled==null){ enabled=oldUser.getEnabled(); }
        if(user.getIseditor()==null){ iseditor=oldUser.getIseditor(); }
        String postContent="{\"username\":\""+username+"\", \"password\":\""+password+"\", \"enabled\":"+enabled.toString()+",\"iseditor\":"+iseditor.toString()+"}";
        DatabaseUtils.sendPutRequest(Url, postContent);
    }

    public void deleteUser(User user) throws JsonProcessingException {
        Long id=0L;
        id=user.getId();
        if(id==null){
            System.out.print("id is empty");
            User oldUser = getUser(user.getUsername(),null);
            if(oldUser==null){
                System.out.print("User does not exist");
                return;
            }
            id=oldUser.getId();
            System.out.print(id);
        }
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User/"+id;
        DatabaseUtils.sendDeleteRequest(Url);
    }

}
