package com.example.demo.Utils;


import com.example.demo.Entity.User;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class DatabaseUtils {
    public static String sendPostRequest(String url, String postContent) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;

        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<String> requestEntity = new HttpEntity<>(postContent, headers);

        //执行HTTP请求
        ResponseEntity<String> postForEntity = client.postForEntity(url, requestEntity, String.class);
        return postForEntity.getBody();
    }

    public static String sendGetRequest(String url){
        RestTemplate client = new RestTemplate();
        //执行HTTP请求
        ResponseEntity<String> getForEntity = client.getForEntity(url, String.class);
        return getForEntity.getBody();
    }

    public static void sendPutRequest(String url, String postContent) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.PUT;

        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<String> requestEntity = new HttpEntity<>(postContent, headers);

        //执行HTTP请求
       client.put(url, requestEntity, String.class);
    }

    public static void sendDeleteRequest(String url) {
        RestTemplate client = new RestTemplate();

        //执行HTTP请求
        client.delete(url, String.class);
    }
}
