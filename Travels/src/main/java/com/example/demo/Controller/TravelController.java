package com.example.demo.Controller;

import com.example.demo.Entity.Travel;
import com.example.demo.Service.TravelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TravelController {
    @Autowired
    private TravelService travelService;

    @PostMapping(value = "travel")
    public Travel createTravel(@RequestParam(value = "username") String username,
                                @RequestBody Travel travel) throws JsonProcessingException {
        return travelService.createTravel(username,travel);
    }
}
