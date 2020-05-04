package com.example.demo.Service;

import com.example.demo.Entity.Comment;
import com.example.demo.Entity.Report;
import com.example.demo.Utils.DatabaseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    public Report createReport(Report report) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Report";
        if(report.getTravelid()==null || report.getUserid()==null){
            return null;
        }
        String postContent="{\"travelid\":"+report.getTravelid()
                            +",\"userid\":"+report.getUserid()
                            +",\"content\":\""+report.getContent()+"\"}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(DatabaseUtils.sendPostRequest(Url, postContent) , Report.class);
    }

    public List<Report> getReport(Long travelid) throws JsonProcessingException {
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Report";

        if(travelid!=null){
            Url+="/?Report.travelid="+travelid.toString();
        }
        ObjectMapper mapper = new ObjectMapper();
        String result=DatabaseUtils.sendGetRequest(Url);
        System.out.print(result);
        if (result.equals("{}")) {
            return null;
        }
        return mapper.readValue(result.substring(result.indexOf("["),result.lastIndexOf("]")+1),new TypeReference<List<Report>>(){});
    }

    public void deleteReport(Report report) {
        Long id=report.getId();
        String Url="http://120.26.184.198:8080/Entity/U36dc49a17fa065/travel/Report/"+id.toString();
        DatabaseUtils.sendDeleteRequest(Url);
    }
}
