package com.example.demo.Utils;


import com.example.demo.Entity.User;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class DatabaseUtils {
    public static String sendPostRequest(String url, String postContent){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;

        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<String> requestEntity = new HttpEntity<>(postContent, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
//        ResponseEntity<ResultVO> response = client.exchange(url, method, requestEntity, ResultVO.class);
        ResponseEntity<String> postForEntity = client.postForEntity(url, requestEntity, String.class);

        return postForEntity.getBody();
    }
}
