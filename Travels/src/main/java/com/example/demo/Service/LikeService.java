package com.example.demo.Service;

import com.example.demo.Entity.Like;
import com.example.demo.Entity.Report;
import com.example.demo.Entity.Travel;
import com.example.demo.Utils.DatabaseUtils;
import com.example.demo.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LikeService {
    @Autowired
    private TravelService travelService;
    @Autowired
    private WebSocketServer webSocketServer;

    public Like createLike(Like like) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Like";
        if(like.getTravelid()==null || like.getUserid()==null){
            return null;
        }
        String postContent="{\"travelid\":"+like.getTravelid()
                +",\"userid\":"+like.getUserid()
                +"}";
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendPostRequest(Url, postContent);

        Long id=like.getTravelid();
        Travel travel=travelService.getTravelById(id);
        Map<String,Object> map=new HashMap<>();
        map.put("username",travel.getUsername());
        map.put("massage","您的游记 "+travel.getTitle()+" 有了新的点赞");
        map.put("title",travel.getTitle());
        map.put("travelId",id);
        map.put("type","comment");
        webSocketServer.sendMessage(travel.getUsername(),map.toString());

        return mapper.readValue(result, Like.class);
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

    public List<Like> getLikeByuserid(Long userid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Like";

        if(userid!=null){
            Url+="/?Like.userid="+userid.toString();
        }
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendGetRequest(Url);
//        System.out.print(result);
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

