package com.example.demo.Controller;

import com.example.demo.Entity.Travel;
import com.example.demo.Service.TravelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@RestController
public class TravelController {
    @Autowired
    private TravelService travelService;

    //travel中需要包含title、content、username、state
    //state：0为未审核(未提交），1为审核中，2为审核通过，3为审核未通过
    @PostMapping(value = "travel")
    public Travel createTravel(@RequestBody Travel travel) throws JsonProcessingException {
        return travelService.createTravel(travel);
    }

    @GetMapping(value = "travel")
    public List<Travel> getTravelAll(@RequestParam(value = "page", defaultValue = "1")Integer page)throws JsonProcessingException{
        return travelService.getTravelAll(page);
    }

    //查询：username、title、keyword为模糊查询，state为准确查询
    @GetMapping(value = "travel/query")
    public List<Travel> getTravel(@RequestParam(value = "username",required = false) String username,
                                  @RequestParam(value = "title",required = false) String title,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(value = "state",required = false) Integer state,
                                  @RequestParam(value = "page", defaultValue = "1")Integer page) throws JsonProcessingException{
        return travelService.getTravel(username,title,keyword,state,page);
    }

    @GetMapping(value = "travel/{id}")
    public Travel getTravelById(@PathVariable("id") Long id) throws JsonProcessingException {
        return travelService.getTravelById(id);
    }

    //修改title、content、keyword
    //travel中必须包含id
    @PostMapping(value = "travel/change")
    public void travelChange(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.travelChange(travel);
    }

    //改state
    //travel中必须包含id
    @PostMapping(value = "travel/state")
    public void travelState(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.travelState(travel);
    }

    //删除travel
    //travel中必须包含id
    @PostMapping(value = "travel/deletetravel")
    public void deleteTravel(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.deletetravel(travel);
    }
}
