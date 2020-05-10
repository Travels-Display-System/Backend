package com.example.demo.Service;

import com.example.demo.Entity.*;
import com.example.demo.Utils.DatabaseUtils;
import com.example.demo.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TravelService {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketServer webSocketServer;//注入socket服务

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    private static Map<User,List<String>> user_label = new ConcurrentHashMap<>();
    private static Map<String,List<String>> recommend_keywords=new ConcurrentHashMap<>();

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
        if(state==null){
            state=0;
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

    public List<Travel> getTravelAll(Integer page,Long userid) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        String result=DatabaseUtils.sendGetRequest(Url);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();
        Map<Travel,Integer> travel_score=new HashMap<>();
        User user=userService.getUser(null,userid);

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            List<Keyword> newKeywordList=mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            newTravel.keywordList=newKeywordList;
            travels.add(newTravel);
            Integer score=0;
            //没有输入userid就不筛选
            if(user==null){
                List<Like> likes=likeService.getLike(newTravel.getId());
                if(likes!=null) score+=likes.size();
                List<Comment> comments=commentService.getCommentByTravelId(newTravel.getId());
                if(comments!=null) score+=comments.size();
                travel_score.put(newTravel,score);
                continue;
            }
            String username=user.getUsername();
            if(recommend_keywords.get(username)==null){
                similarUser(user);
            }
            List<String> rk=recommend_keywords.get(username);
            if(rk.size()!=0) {
                for (Keyword keyword : newKeywordList) {
                    if (rk.contains(keyword.getKeyword())) {
                        score += 1;
                    }
                }
            }
            List<Like> likes=likeService.getLike(newTravel.getId());
            if(likes!=null) score+=likes.size();
            travel_score.put(newTravel,score);
        }
        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp())+travel_score.get(o2)-travel_score.get(o1);
            }
        });
        Integer begin=(page-1)*20;
        Integer end=begin+20;
        if(travels.size()<begin){
            return null;
        }
        if(travels.size()<end){
            end=travels.size();
        }
        return travels.subList(begin,end);
    }

    public List<Travel> getTravel(String username, String title, String keyword, Integer state, Integer page,Long userid) throws JsonProcessingException {
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
        System.out.print(result);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();
        Map<Travel,Integer> travel_score=new HashMap<>();
        User user=userService.getUser(null,userid);

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            List<Keyword> newKeywordList=mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            newTravel.keywordList=newKeywordList;
            travels.add(newTravel);
            Integer score=0;
            //没有输入userid就不筛选
            if(user==null){
                List<Like> likes=likeService.getLike(newTravel.getId());
                if(likes!=null) score+=likes.size();
                travel_score.put(newTravel,score);
                continue;
            }
            String usernamenow=user.getUsername();
            if(recommend_keywords.get(usernamenow)==null){
                similarUser(user);
            }
            List<String> rk=recommend_keywords.get(usernamenow);
            if(rk.size()!=0) {
                for (Keyword keyword1 : newKeywordList) {
                    if (rk.contains(keyword1.getKeyword())) {
                        score += 1;
                    }
                }
            }
            List<Like> likes=likeService.getLike(newTravel.getId());
            if(likes!=null) score+=likes.size();
            List<Comment> comments=commentService.getCommentByTravelId(newTravel.getId());
            if(comments!=null) score+=comments.size();
            travel_score.put(newTravel,score);
        }
        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp())+travel_score.get(o2)-travel_score.get(o1);
            }
        });
        Integer begin=(page-1)*20;
        Integer end=begin+20;
        if(travels.size()<begin){
            return null;
        }
        if(travels.size()<end){
            end=travels.size();
        }
        return travels.subList(begin,end);
    }

    public List<Travel> getTravelSelfyes(String username, Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        if(username!=null){
            User user=userService.getUser(username,null);
            if(user!=null){
                Url+="/?Travel.username="+username;
            }
        }
        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            newTravel.keywordList= mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            if(newTravel.getState()==2) {
                if( (username!=null && newTravel.getUsername().equals(username)) || username==null)
                    travels.add(newTravel);
            }
        }

        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        return travels;
    }

    public List<Travel> getTravelSelfno(String username, Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        if(username!=null){
            User user=userService.getUser(username,null);
            if(user!=null){
                Url+="/?Travel.username="+username;
            }
        }
        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            newTravel.keywordList= mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            if(newTravel.getState()!=2) {
                if( (username!=null && newTravel.getUsername().equals(username)) || username==null)
                    travels.add(newTravel);
            }
        }

        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        return travels;
    }

    public Travel getTravelById(Long id) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
        String result=DatabaseUtils.sendGetRequest(Url);
        if(result.equals("[]")||result.equals("")||result.equals("{}")){
            return null;
        }
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
            map.put("username",newtravel.getUsername());
            map.put("massage","您的游记 "+newtravel.getTitle()+" 审核已通过");
            map.put("title",newtravel.getTitle());
            map.put("travelId",id);
        }
        if(state==3){
            map.put("username",newtravel.getUsername());
            map.put("massage","您的游记 "+newtravel.getTitle()+" 审核未通过");
            map.put("title",newtravel.getTitle());
            map.put("travelId",id);
        }
        webSocketServer.sendMessage(newtravel.getUsername(),map.toString());
    }

    public String deletetravel(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        if(oldtravel==null){
            return "游记不存在";
        }
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        String postContent="{\"title\":\""+oldtravel.getTitle()
                +"\", \"content\":\""+oldtravel.getContent()
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":[]"
                +",\"advice\":\""+oldtravel.getAdvice()
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":0}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();

        DatabaseUtils.sendPutRequest(Url, postContent);


        //删除原有的keyword
        for(Keyword oldkeyword:oldkeywords){
            Long tmpid=oldkeyword.getId();
            keywordService.deleteKeyword(tmpid);
        }

        //删除travel
       Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
       DatabaseUtils.sendDeleteRequest(Url);
       return "删除游记成功";
    }

    public List<Map> getTravelSimple(String username, String title, String keyword, Integer state, Integer page,Long userid) throws JsonProcessingException {
        List<Map> result=new ArrayList<>();
        List<Travel> travels=getTravel(username,title,keyword,state,page,userid);
        if(travels==null){
            return null;
        }
        for(Travel travel: travels){
            Map<String,Object> map=new HashMap<>();
            map.put("id",travel.getId());
            map.put("title",travel.getTitle());
            map.put("username",travel.getUsername());
            result.add(map);
        }
        return result;
    }

    public List<Map> getTravelSimpleSelfyes(String username,Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        if(username!=null){
            User user=userService.getUser(username,null);
            if(user!=null){
                Url+="/?Travel.username="+username;
            }
        }

        String result=DatabaseUtils.sendGetRequest(Url);
//        System.out.print(result);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            newTravel.keywordList= mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            if(newTravel.getState()==2) {
                if( (username!=null && newTravel.getUsername().equals(username)) || username==null)
                travels.add(newTravel);
            }
        }

        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        System.out.print(travels);
        List<Map> resultmap=new ArrayList<>();
        for(Travel travel: travels){
            Map<String,Object> map=new HashMap<>();
            map.put("id",travel.getId());
            map.put("title",travel.getTitle());
            map.put("username",travel.getUsername());
            resultmap.add(map);
        }
        System.out.print(resultmap);
        return resultmap;
    }

    public List<Map> getTravelSimpleSelfno(String username,Integer page) throws JsonProcessingException {
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel";
        if(username!=null){
            User user=userService.getUser(username,null);
            if(user!=null){
                Url+="/?Travel.username="+username;
            }
        }

        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        if(result.equals("{}")){
            return null;
        }
        String [] strtravels=result.substring(result.indexOf("[")+1,result.lastIndexOf("]")).split("]},");
        ObjectMapper mapper = new ObjectMapper();
        List<Travel> travels=new ArrayList<>();

        for(String strtravel:strtravels){
            strtravel=strtravel+"]}";
            Travel newTravel=mapper.readValue(strtravel , Travel.class);
            System.out.print(strtravel);
            newTravel.keywordList= mapper.readValue(strtravel.substring(strtravel.indexOf("["),strtravel.lastIndexOf("]")+1),new TypeReference<List<Keyword>>(){});
            if(newTravel.getState()!=2) {
                if( (username!=null && newTravel.getUsername().equals(username)) || username==null)
                    travels.add(newTravel);
            }
        }

        travels.sort(new Comparator<Travel>() {
            @Override
            public int compare(Travel o1, Travel o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        List<Map> resultmap=new ArrayList<>();
        for(Travel travel: travels){
            Map<String,Object> map=new HashMap<>();
            map.put("id",travel.getId());
            map.put("title",travel.getTitle());
            map.put("username",travel.getUsername());
            resultmap.add(map);
        }
        return resultmap;
    }

    public String deleteTravelByAdmin(Travel travel) throws JsonProcessingException {
        Long id = travel.getId();
        Travel oldtravel = getTravelById(id);
        if(oldtravel==null){
            return "游记不存在";
        }
        List<Keyword> oldkeywords=oldtravel.getKeywordList();

        String postContent="{\"title\":\""+oldtravel.getTitle()
                +"\", \"content\":\""+oldtravel.getContent()
                +"\", \"username\":\""+oldtravel.getUsername()
                +"\",\"keywords\":[]"
                +",\"advice\":\""+oldtravel.getAdvice()
                +"\", \"timestamp\":\""+oldtravel.getTimestamp()
                +"\", \"state\":0}";

        System.out.print(postContent);
        String Url = "http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Travel/"+id.toString();
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
        map.put("username",oldtravel.getUsername());
        map.put("massage","您的游记 "+oldtravel.getTitle()+" 已被管理员删除");
        map.put("title",oldtravel.getTitle());
        map.put("travelId",id);

        webSocketServer.sendMessage(oldtravel.getUsername(),map.toString());

        return "删除游记成功";
    }

    public void UserLabel() throws JsonProcessingException {
//        if(user_label.size()!=0) return;
        System.out.print("UserLabel");
        List<User> users=userService.getUserAll();
        for(User user:users){
            List<String> userKeyword=new ArrayList<>();
            Long userid=user.getId();
            System.out.print(userid);
            List<Like> likes=likeService.getLikeByuserid(userid);
            if(likes!=null) {
                for (Like like : likes) {
                    Long travelid = like.getTravelid();
                    Travel travel = getTravelById(travelid);
                    if (travel == null) continue;
                    List<Keyword> keywords = travel.getKeywordList();
                    if (keywords == null) continue;
                    for (Keyword k : keywords) {
                        userKeyword.add(k.getKeyword());
                    }
                }
            }
            List<Comment> comments=commentService.getCommentByTravelId(userid);
            if(comments!=null) {
                for (Comment comment : comments) {
                    Long travelid = comment.getTravelid();
                    Travel travel = getTravelById(travelid);
                    if (travel == null) continue;
                    List<Keyword> keywords = travel.getKeywordList();
                    if (keywords == null) continue;
                    for (Keyword k : keywords) {
                        userKeyword.add(k.getKeyword());
                    }
                }
            }
            user_label.put(user,userKeyword);
            System.out.print(user.getId().toString()+user.getUsername()+" :\n");
            for(String k:userKeyword){
                System.out.print(k+", ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    //计算用户相似度,并根据用户相似度进行关键词推荐
    public void similarUser(User user) throws JsonProcessingException {
//        if(recommend_keywords.get(user.getUsername())!=null) return;
        UserLabel();
        int N=user_label.size();
//        if(N==0){
//            UserLabel();
//            N=user_label.size();
//        }
        if(N==0) return;
        int[][] sparseMatrix = new int[N][N];
        //建立用户稀疏矩阵，用于用户相似度计算【相似度矩阵】
        Map<String, Integer> userItemLength = new HashMap<>();
        //存储每一个用户对应的不同关键词总数
        Map<String, Map<String,Integer>> itemUserCollection = new HashMap<>();
        //建立关键词到用户的倒排表
        Set<String> items = new HashSet<>();
        //辅助存储关键词集合
        Map<String, Integer> userID = new HashMap<>();
        //辅助存储每一个用户的用户ID映射
        Map<Integer, String> idUser = new HashMap<>();
        //辅助存储每一个ID对应的用户映射
        Set set = user_label.keySet();
        int i=0;
        for (Iterator iter = set.iterator(); iter.hasNext();i++){
            User every_user=(User) iter.next();
            System.out.print(every_user.getUsername()+" ");
            List<String> keywords=user_label.get(every_user);
            System.out.print(keywords.toString()+"\n");
            int length=keywords.size();
            userItemLength.put(every_user.getUsername(), length);
            userID.put(every_user.getUsername(), i);
            //用户ID与稀疏矩阵建立对应关系
            idUser.put(i, every_user.getUsername());
            //建立关键词--用户倒排表
            for (String keyword : keywords){
                if(items.contains(keyword)){
                    //如果已经包含对应的关键词--用户映射，直接添加对应的用户
                    if(itemUserCollection.get(keyword).get(every_user.getUsername())==null){
                        itemUserCollection.get(keyword).put(every_user.getUsername(),1);
                    }
                    else{
                        Integer num=itemUserCollection.get(keyword).get(every_user.getUsername());
                        itemUserCollection.get(keyword).put(every_user.getUsername(),num+1);
                    }
                    System.out.print(every_user.getUsername()+" "+keyword+"\n");
                    System.out.println(itemUserCollection.toString()+"\n");
                } else{
                    //否则创建对应关键词--用户集合映射
                    items.add(keyword);
                    itemUserCollection.put(keyword, new HashMap<>());
                    //创建关键词--用户倒排关系
                    itemUserCollection.get(keyword).put(every_user.getUsername(),1);
                }
            }
        }
        System.out.println(itemUserCollection.toString());
        //计算相似度矩阵【稀疏】
        Set<Map.Entry<String, Map<String,Integer>>> entrySet = itemUserCollection.entrySet();
        Iterator<Map.Entry<String, Map<String,Integer>>> iterator = entrySet.iterator();
        while(iterator.hasNext()){
            Map<String,Integer> commonUsers = iterator.next().getValue();
            Set entrySet1 = commonUsers.keySet();
            for(Iterator iter = entrySet1.iterator(); iter.hasNext();)
            {
                String user_u = (String)iter.next();
                Integer value = (Integer)commonUsers.get(user_u);
                System.out.println(user_u+"===="+value);
                for(Iterator iter1 = entrySet1.iterator(); iter1.hasNext();){
                    String user_v = (String)iter1.next();
                    Integer value1 = (Integer)commonUsers.get(user_v);
                    if(user_u.equals(user_v)){
                        continue;
                    }
                    sparseMatrix[userID.get(user_u)][userID.get(user_v)] += value*value1;
                }
            }
        }
        System.out.println(userItemLength.toString());
        System.out.println("Input the user for recommendation:<eg:A>");
        String recommendUser = user.getUsername();
        System.out.println(userID.get(recommendUser));
        //计算用户之间的相似度【余弦相似性】
        int recommendUserId = userID.get(recommendUser);
        for (int j = 0;j < sparseMatrix.length; j++) {
            if(j != recommendUserId){
                System.out.println(idUser.get(recommendUserId)+"--"+idUser.get(j)+"相似度:"+sparseMatrix[recommendUserId][j]/Math.sqrt(userItemLength.get(idUser.get(recommendUserId))*userItemLength.get(idUser.get(j))));
            }
        }
        List<String> newkeyword=new ArrayList<>();
        //计算指定用户recommendUser的关键词推荐度
        for (String item: items){
            //遍历每一件物品
            Map<String,Integer> users_map = itemUserCollection.get(item);
            Set entrySet1 = users_map.keySet();
            double itemRecommendDegree = 0.0;
            for(Iterator iter = entrySet1.iterator(); iter.hasNext();) {
                String user1 = (String) iter.next();
                itemRecommendDegree += sparseMatrix[userID.get(recommendUser)][userID.get(user1)] / Math.sqrt(userItemLength.get(recommendUser) * userItemLength.get(user1));
            }
            if(itemRecommendDegree>=1)
                newkeyword.add(item);
            System.out.println("The item "+item+" for "+recommendUser +"'s recommended degree:"+itemRecommendDegree);

        }
        recommend_keywords.put(recommendUser,newkeyword);
        System.out.print(recommend_keywords.toString());
    }

}

