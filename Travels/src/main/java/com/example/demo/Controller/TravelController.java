package com.example.demo.Controller;

import com.example.demo.Entity.Travel;
import com.example.demo.Entity.User;
import com.example.demo.Service.TravelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    //含推荐
    @GetMapping(value = "travel")
    public List<Travel> getTravelAll(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                     @RequestParam(value = "userid",required = false)Long userid)throws JsonProcessingException{
        return travelService.getTravelAll(page,userid);
    }

    //查询：username、title、keyword为模糊查询，state为准确查询，含推荐
    @GetMapping(value = "travel/query")
    public List<Travel> getTravel(@RequestParam(value = "username",required = false) String username,
                                  @RequestParam(value = "title",required = false) String title,
                                  @RequestParam(value = "keyword",required = false) String keyword,
                                  @RequestParam(value = "state",required = false) Integer state,
                                  @RequestParam(value = "page", defaultValue = "1")Integer page,
                                  @RequestParam(value = "userid",required = false)Long userid) throws JsonProcessingException{
        return travelService.getTravel(username, title, keyword, state, page,userid);
    }

    //查询：username、title、keyword为模糊查询，state为准确查询,只返回username/title/id，含推荐
    @GetMapping(value = "travel/query/simple")
    public List<Map> getTravelSimple(@RequestParam(value = "username",required = false) String username,
                                     @RequestParam(value = "title",required = false) String title,
                                     @RequestParam(value = "keyword",required = false) String keyword,
                                     @RequestParam(value = "state",required = false) Integer state,
                                     @RequestParam(value = "page", defaultValue = "1")Integer page,
                                     @RequestParam(value = "userid",required = false)Long userid) throws JsonProcessingException{
        return travelService.getTravelSimple(username, title, keyword, state, page,userid);
    }


    //作者查询：username为准确查询；
    // 编辑和管理员在不输入username的时候，获得所有travel
    @GetMapping(value = "travel/query/self")
    public List<Travel> getTravelSelf(@RequestParam(value = "username",required = false) String username,
                                      @RequestParam(value = "page", defaultValue = "1")Integer page) throws JsonProcessingException{
        return travelService.getTravelSelf(username,page);
    }

    //作者查询：username为准确查询,只返回username/title/id；
    // 编辑和管理员在不输入username的时候，获得所有travel
    @GetMapping(value = "travel/query/simple/self")
    public List<Map> getTravelSimpleSelf(@RequestParam(value = "username",required = false) String username,
                                         @RequestParam(value = "page", defaultValue = "1")Integer page) throws JsonProcessingException{
        return travelService.getTravelSimpleSelf(username,page);
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


    //用户删除自己的travel
    //travel中必须包含id
    @PostMapping(value = "travel/deletetravel")
    public void deleteTravel(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.deletetravel(travel);
    }

    //编辑改state
    //travel中必须包含id
    @PostMapping(value = "travel/state/admin")
    public void travelState(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.travelState(travel);
    }

    //编辑修改advice
    //travel中必须包含id
    @PostMapping(value = "travel/change/admin")
    public void adviceByAdmin(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.adviceByAdmin(travel);
    }

    //管理员删除travel
    //travel中必须包含id
    @PostMapping(value = "travel/deletetravel/admin")
    public void deleteTravelByAdmin(@RequestBody Travel travel)throws JsonProcessingException{
        travelService.deleteTravelByAdmin(travel);
    }

    @PostMapping(value = "travel/test")
    public void test(@RequestBody User user)throws JsonProcessingException{
        System.out.print(user.getUsername());
        travelService.similarUser(user);
    }

}
