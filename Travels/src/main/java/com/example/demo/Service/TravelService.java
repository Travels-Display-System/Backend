package com.example.demo.Service;

import com.example.demo.Entity.Keyword;
import com.example.demo.Entity.Travel;
import com.example.demo.Entity.User;
import com.example.demo.Utils.DatabaseUtils;
import com.example.demo.WebSocketServer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TravelService {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketServer webSocketServer;//注入socket服务

    public Travel createTravel(Travel travel) throws JsonProcessingException {
        String title=travel.getTitle();
        String content=travel.getContent();
        String username=travel.getUsername();
        Integer state=travel.getState();
        //获取user，user不存在则返回
        User user=userService.getUser(username,null);
        if(user==null){
            System.out.print("User does not exit");
            return null;
        }
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
                +"\", \"username\":\""+username
                +"\",\"keywords\":"+keywordstr
                +", \"timestamp\":\""+timestamp
                +"\", \"state\":"+state.toString()+"}";

        //发送请求并返回实体
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendPostRequest(Url, postContent);
        Travel newTravel=mapper.readValue(result , Travel.class);
        List<Keyword> newKeywordList=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
        newTravel.keywordList=newKeywordList;
        return newTravel;
    }

    public List<Travel> getTravelAll(Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        String result=DatabaseUtils.sendGetRequest(Url);
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();
        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            List<Keyword> newKeywordList=mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            newTravel.keywordList=newKeywordList;
            travels.add(newTravel);
        }
        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        Integer begin=(page-1)*20+1;
        Integer end=begin+20;
        if(travels.size()<begin){
            return null;
        }
        if(travels.size()<end){
            end=travels.size();
        }
        return travels.subList(begin,end);
    }

    public List<Travel> getTravel(String username, String title, String keyword, Integer state, Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        if(username!=null){
            User user=userService.getUser(username,null);
            if(user!=null){
                Url+="/?Travel.username=(like)"+username;
            }
        }
        else if(title!=null){
            Url+="?Travel.title=(like)"+title;
        }
        else if(keyword!=null){
            Url+="?Travel.keywords.keyword=(like)"+keyword;
        }
        else if(state!=null){
            Url+="?Travel.state="+state;
        }
        String result=DatabaseUtils.sendGetRequest(Url);
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();
        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            List<Keyword> newKeywordList=mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            newTravel.keywordList=newKeywordList;
            travels.add(newTravel);
        }

        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        Integer begin=(page-1)*20+1;
        Integer end=begin+20;
        if(travels.size()<begin){
            return null;
        }
        if(travels.size()<end){
            end=travels.size();
        }
        return travels.subList(begin,end);
    }

    public Travel getTravelById(Long id) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        String result=DatabaseUtils.sendGetRequest(Url);
        ObjectMapper mapper = new ObjectMapper();
        Travel newTravel=mapper.readValue(result , Travel.class);
        List<Keyword> newKeywordList=mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
        newTravel.keywordList=newKeywordList;
        return newTravel;
    }

    public void travelChange(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        String advice=travel.getAdvice();
        String title=travel.getTitle();
        String content=travel.getContent();
        if(content==null || title==null){
            return;
        }
        List<Keyword> keywords=travel.getKeywordList();
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        //创建关键词
        String keywordstr = "[";
        if(keywords.size()==0){
            keywordstr="[]";
        }
        else {
            for (Keyword keyword : keywords) {
                Keyword tmpkey = keywordService.createKeyword(keyword.getKeyword());
                Long tmpid = tmpkey.getId();
                keywordstr += "{\"id\":" + tmpid.toString() + ",\"keyword\":\"" + tmpkey + "\"},";
            }
            keywordstr = keywordstr.substring(0, keywordstr.length() - 1);
            keywordstr += "]";
        }
        String postContent="{\"title\":\""+title
                +"\", \"content\":\""+content
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":"+keywordstr
                +",\"advice\":\""+advice
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":"+oldtravel.getState()+"}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        ObjectMapper mapper = new ObjectMapper();
        DatabaseUtils.sendPutRequest(Url, postContent);
        //删除原有的keyword
        for(Keyword oldkeyword:oldkeywords){
            Long tmpid=oldkeyword.getId();
            keywordService.deleteKeyword(tmpid);
        }


    }

    //
    public void adviceByAdmin(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        String advice=travel.getAdvice();
        if(advice.equals("")){
            return;
        }
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        String keywordstr = "[";
        for (Keyword keyword : oldkeywords) {
            keywordstr += "{\"id\":" + keyword.getId() + ",\"keyword\":\"" + keyword.getKeyword() + "\"},";
        }
        keywordstr = keywordstr.substring(0, keywordstr.length() - 1);
        keywordstr += "]";

        String postContent="{\"title\":\""+oldtravel.getTitle()
                +"\", \"content\":\""+oldtravel.getContent()
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":"+keywordstr
                +",\"advice\":\""+advice
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":"+oldtravel.getState()+"}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        ObjectMapper mapper = new ObjectMapper();
        DatabaseUtils.sendPutRequest(Url, postContent);

        Map<String,Object> map=new HashMap<>();
        map.put("username",oldtravel.getUsername());
        map.put("massage","您的游记"+oldtravel.getTitle()+"有新的审核意见");
        map.put("title",oldtravel.getTitle());
        map.put("travelId",id);

        webSocketServer.sendMessage(oldtravel.getUsername(),map.toString());
    }
    public void travelState(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel newtravel = getTravelById(id);
        Integer state=travel.getState();
        if(state==null){
           return ;
        }
        String keywordstr = "[";
        for (Keyword keyword : newtravel.keywordList) {
            Long tmpid = keyword.getId();
            String tmpkeyword = keyword.getKeyword();
            keywordstr += "{\"id\":" + tmpid.toString() + ",\"keyword\":\""+tmpkeyword+"\"},";
        }
        keywordstr=keywordstr.substring(0,keywordstr.length()-1);
        keywordstr+="]";
        String postContent="{\"title\":\""+newtravel.getTitle()
                +"\", \"content\":\""+newtravel.getContent()
                +"\", \"username\":\""+newtravel.getUsername()
                +"\",\"keywords\":"+keywordstr
                +",\"advice\":\""+newtravel.getAdvice()
                +"\", \"timestamp\":\""+newtravel.getTimestamp()
                +"\", \"state\":"+state+"}";
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        ObjectMapper mapper = new ObjectMapper();
        DatabaseUtils.sendPutRequest(Url, postContent);
        Map<String,Object> map=new HashMap<>();
        if(state==2){
            map.put("username",newtravel.getContent());
            map.put("massage","您的游记"+newtravel.getTitle()+"审核已通过");
            map.put("title",newtravel.getTitle());
            map.put("travelId",id);
        }
        if(state==3){
            map.put("username",newtravel.getContent());
            map.put("massage","您的游记"+newtravel.getTitle()+"审核未通过");
            map.put("title",newtravel.getTitle());
            map.put("travelId",id);
        }
        webSocketServer.sendMessage(newtravel.getUsername(),map.toString());
    }

    public void deletetravel(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        String postContent="{\"title\":\""+oldtravel.getTitle()
                +"\", \"content\":\""+oldtravel.getContent()
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":[]"
                +",\"advice\":\""+oldtravel.getAdvice()
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":"+oldtravel.getState()+"}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        ObjectMapper mapper = new ObjectMapper();
        DatabaseUtils.sendPutRequest(Url, postContent);

        //删除原有的keyword
        for(Keyword oldkeyword:oldkeywords){
            Long tmpid=oldkeyword.getId();
            keywordService.deleteKeyword(tmpid);
        }

        //删除travel
       Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
       DatabaseUtils.sendDeleteRequest(Url);
    }

    public List<Map> getTravelSimple(String username, String title, String keyword, Integer state, Integer page) throws JsonProcessingException {
        List<Map> result=new ArrayList<>();
        List<Travel> travels=getTravel(username,title,keyword,state,page);
        for(Travel travel: travels){
            Map<String,Object> map=new HashMap<>();
            map.put("id",travel.getId());
            map.put("title",travel.getTitle());
            map.put("username",travel.getUsername());
            result.add(map);
        }
        return result;
    }

    public void deleteTravelByAdmin(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        String postContent="{\"title\":\""+oldtravel.getTitle()
                +"\", \"content\":\""+oldtravel.getContent()
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":[]"
                +",\"advice\":\""+oldtravel.getAdvice()
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":"+oldtravel.getState()+"}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        ObjectMapper mapper = new ObjectMapper();
        DatabaseUtils.sendPutRequest(Url, postContent);

        //删除原有的keyword
        for(Keyword oldkeyword:oldkeywords){
            Long tmpid=oldkeyword.getId();
            keywordService.deleteKeyword(tmpid);
        }

        //删除travel
        Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        DatabaseUtils.sendDeleteRequest(Url);

        Map<String,Object> map=new HashMap<>();
        map.put("username",travel.getContent());
        map.put("massage","您的游记"+oldtravel.getTitle()+"已被管理员删除");
        map.put("title",oldtravel.getTitle());
        map.put("travelId",id);
        webSocketServer.sendMessage(oldtravel.getUsername(),map.toString());
    }
}

