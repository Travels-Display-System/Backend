package com.example.demo.Service;

import com.example.demo.Entity.Like;
import com.example.demo.Entity.Report;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeService {

    public Like createLike(Like like) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Like";
        if(like.getTravelid()==null || like.getUserid()==null){
            return null;
        }
        String postContent="{\"travelid\":"+like.getTravelid()
                +",\"userid\":"+like.getUserid()
                +"}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , Like.class);
    }

    public List<Like> getLike(Long travelid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Like";

        if(travelid!=null){
            Url+="/?Like.travelid="+travelid.toString();
        }
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        if (result.equals("{}")) {
            return null;
        }
        return mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Like>>(){});
    }

    public void deleteLike(Like like){
        Long id=like.getId();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Like/"+id.toString();
        DatabaseUtils.sendDeleteRequest(Url);
    }
}

