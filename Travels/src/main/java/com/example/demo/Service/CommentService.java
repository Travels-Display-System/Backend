package com.example.demo.Service;

import com.example.demo.Entity.Comment;
import com.example.demo.Entity.Keyword;
import com.example.demo.Entity.Travel;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    public Comment createCommnt(Comment comment) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment";
       if(comment.getTravelid()==null){
           return null;
       }
        String postContent="{\"travelid\":"+comment.getTravelid()+",\"content\":\""+comment.getContent()+"\"}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , Comment.class);
    }
//    http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment/?Comment.travelid=1588569726698
//    http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment/?Comment.travelid=1588569726698
    public List<Comment> getCommentByTravelId(Long travelid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Comment/?Comment.travelid="+travelid.toString();
        ObjectMapper mapper = new ObjectMapper();
        System.out.print(Url);
        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        return mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Comment>>(){});
    }
}
