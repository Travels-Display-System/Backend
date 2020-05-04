package com.example.demo.Controller;

import com.example.demo.Entity.Comment;
import com.example.demo.Service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping(value = "comment")
    public Comment createComment(@RequestBody Comment comment) throws JsonProcessingException {
        System.out.print(comment.getTravelid());
        return commentService.createCommnt(comment);
    }

    @GetMapping(value = "comment/{travelid}")
    public List<Comment> getCommentByTravelId(@PathVariable("travelid") Long travelid) throws JsonProcessingException {
        System.out.print(travelid);
        return commentService.getCommentByTravelId(travelid);
    }


}
