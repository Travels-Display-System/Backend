package com.example.demo.Service;

import com.example.demo.Entity.Keyword;
import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordService {

    public Keyword createKeyword(String keyword) throws JsonProcessingException {
           String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Keywords";
           String postContent="{\"keyword\":\""+keyword+"\"}";
           ObjectMapper mapper = new ObjectMapper();
           String result=DatabaseUtils.sendPostRequest(Url, postContent);
        return mapper.readValue( result, Keyword.class);

    }

    public void deleteKeyword(Long id){
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Keywords/"+id.toString();
        DatabaseUtils.sendDeleteRequest(Url);
    }
}
