package com.example.demo.Controller;

import com.example.demo.Entity.Like;
import com.example.demo.Entity.Report;
import com.example.demo.Service.LikeService;
import com.example.demo.Service.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LikeController {
    @Autowired
    private LikeService likeService;

    @GetMapping(value = "like/query")
    public List<Like> getLike(@RequestParam(value = "travelid",required = false) Long travelid) throws JsonProcessingException {
        return likeService.getLike(travelid);
    }

    @PostMapping(value = "like")
    public Like createLike(@RequestBody Like like) throws JsonProcessingException {
        return likeService.createLike(like);
    }

    @PostMapping(value = "like/nolike")
    public void deleteLike(@RequestBody Like like) throws JsonProcessingException {
        likeService.deleteLike(like);
    }
}
