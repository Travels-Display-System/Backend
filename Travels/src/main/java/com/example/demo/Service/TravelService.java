package com.example.demo.Service;

import com.example.demo.Entity.Keyword;
import com.example.demo.Entity.Travel;
import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class TravelService {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserService userService;


    public Travel createTravel(String username,Travel travel) throws JsonProcessingException {
        String title=travel.getTitle();
        String content=travel.getContent();

        //获取user，user不存在则返回
        User user=userService.getUser(username,null);
        if(user==null){
            System.out.print("User does not exit");
            return null;
        }
        Long userId=user.getId();
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        //创建关键词
        List<Long> keywordIds = new ArrayList<>();
        for(Keyword keyword:travel.keywordList){
            Keyword tmpkey=keywordService.createKeyword(keyword.getKeyword());
            keywordIds.add(tmpkey.getId());
        }

        //构造请求体
        String keywordstr="[";
        for(Long id:keywordIds){ keywordstr+="{\"id\":"+id.toString()+"},"; }
        keywordstr=keywordstr.substring(0,keywordstr.length()-1);
        keywordstr+="]";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String timestamp=df.format(new Date());// new Date()为获取当前系统时间

        String postContent="{\"title\":\""+title
                +"\", \"content\":\""+content
                +"\", \"userId\":"+userId.toString()
                +",\"keywords\":"+keywordstr
                +", \"timestamp\":\""+timestamp
                +"\", \"state\":0}";

        //发送请求并返回实体
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendPostRequest(Url, postContent);
        Travel newTravel=mapper.readValue(result , Travel.class);
        List<Keyword> newKeywordList=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
        System.out.print(newKeywordList);
        newTravel.keywordList=newKeywordList;
        return newTravel;
    }


}