package com.example.demo.Controller;

import com.example.demo.Entity.Travel;
import com.example.demo.Service.TravelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TravelController {
    @Autowired
    private TravelService travelService;

    @PostMapping(value = "travel")
    public Travel createTravel(@RequestBody Travel travel) throws JsonProcessingException {
        return travelService.createTravel(travel);
    }

    @GetMapping(value = "travel")
    public List<Travel> getTravelAll()throws JsonProcessingException{
        return travelService.getTravelAll();
    }

    @GetMapping(value = "travel/query")
    public List<Travel> getTravel(@RequestParam(value = "username",required = false) String username,
                                  @RequestParam(value = "title",required = false) String title,
                                  @RequestParam(value = "keyword",required = false) String keyword) throws JsonProcessingException{
        return travelService.getTravel(username,title,keyword);
    }
    @GetMapping(value = "travel/{id}")
    public Travel getTravelById(@PathVariable("id") Long id) throws JsonProcessingException {
        return travelService.getTravelById(id);
    }
    @PostMapping(value = "travel/advice")
    public void travelAdvice(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.travelAdvice(travel);
    }

    @PostMapping(value = "travel/state")
    public void travelState(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.travelState(travel);
    }
}
