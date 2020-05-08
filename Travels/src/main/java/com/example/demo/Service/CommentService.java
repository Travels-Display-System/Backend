package com.example.demo.Service;

import com.example.demo.Entity.Comment;
import com.example.demo.Entity.Keyword;
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
public class CommentService {
    @Autowired
    private TravelService travelService;
    @Autowired
    private WebSocketServer webSocketServer;

    public Comment createCommnt(Comment comment) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment";
       Long id=comment.getTravelid();
        if(comment.getTravelid()==null){
           return null;
       }
        String postContent="{\"travelid\":"+comment.getTravelid()+",\"content\":\""+comment.getContent()+"\"}";
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendPostRequest(Url, postContent);

        Travel travel=travelService.getTravelById(id);
        Map<String,Object> map=new HashMap<>();
        map.put("username",travel.getUsername());
        map.put("massage","您的游记 "+travel.getTitle()+" 有了新的评论");
        map.put("title",travel.getTitle());
        map.put("travelId",id);
        map.put("type","comment");
        webSocketServer.sendMessage(travel.getUsername(),map.toString());

        return mapper.readValue(result , Comment.class);
    }

    public List<Comment> getCommentByTravelId(Long travelid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment/?Comment.travelid="+travelid.toString();
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendGetRequest(Url);
        if(result.equals("{}")) return null;
        return mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Comment>>(){});
    }
    public List<Comment> getCommentByUserId(Long userid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment/?Comment.userid="+userid.toString();
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendGetRequest(Url);
        if(result.equals("{}")) return null;
        return mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Comment>>(){});
    }
}
