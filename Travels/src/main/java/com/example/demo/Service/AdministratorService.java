package com.example.demo.Service;

import com.example.demo.Entity.Administrator;
import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministratorService {
    public Administrator createAdmin(Administrator administrator) throws JsonProcessingException {
        if(administrator.getPassword()==null ||administrator.getUsername()==null){
            return null;
        }
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Administrator";
        //username不能重复
        String username=administrator.getUsername();
        String password=administrator.getPassword();
        Administrator admin=getAdmin(username,null);
        if(admin!=null){
            System.out.print("User has already exist");
            return admin;
        }

        String postContent="{\"username\":\""+username+"\", \"password\":\""+password+"\"}";

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , Administrator.class);
    }

    public Administrator getAdmin(String username,Long id) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Administrator/";
        if(username!=null){
            Url+="?Administrator.username="+username;
            String result = DatabaseUtils.sendGetRequest(Url);
            if(result.equals("{}")){
                return null;
            }
            List<Administrator> admins=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1) , new TypeReference<List<Administrator>>(){/**/});
            return admins.get(0);
        }
        if(id!=null){
            Url+=id;
            String result = DatabaseUtils.sendGetRequest(Url);
            return mapper.readValue(result, Administrator.class);
        }
        return null;
    }

    public void deleteAdmin(Administrator administrator) throws JsonProcessingException {
        Long id=0L;
        id=administrator.getId();
        if(id==null){
            System.out.print("id is empty");
            Administrator oldadmin= getAdmin(administrator.getUsername(),null);
            if(oldadmin==null){
                System.out.print("Admin does not exist");
                return;
            }
            id=oldadmin.getId();
            System.out.print(id);
        }
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Administrator/"+id.toString();
        DatabaseUtils.sendDeleteRequest(Url);
    }

    public List<Administrator> getAdminAll() throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Administrator";
        String result=DatabaseUtils.sendGetRequest(Url);
        if(result.equals("{}")) return null;
        ObjectMapper mapper = new ObjectMapper();
        List<Administrator> admins=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1) , new TypeReference<List<Administrator>>(){/**/});
        return  admins;
    }
}
