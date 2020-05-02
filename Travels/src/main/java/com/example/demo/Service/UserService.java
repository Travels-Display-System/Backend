package com.example.demo.Service;

import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class UserService {

    public String addUser(String username, String password) {

    return addUser(username,password,1,0);
    }

    public String addUser(String username, String password, Integer enabled, Integer iseditor) {

        String authorizeUrl = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/User";

        String postContent="{\"username\": \"dyj\", \"password\": \"dyj\", \"enabled\": 1, \"iseditor\":0}";
//                "{" +
//                "\"username\": " + username +"," +
//                "\"password\": " + password + "," +
//                "\"enabled\": " + enabled +"," +
//                "\"iseditor\":" + iseditor +
//                "}";

        //发送Post数据并返回数据

        return  DatabaseUtils.sendPostRequest(authorizeUrl, postContent);

    }

}
