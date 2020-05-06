package com.example.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/socket/{username}")
public class WebSocketServer {
    /**
     * 全部在线会话
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(@PathParam("username") String name, Session session) {
        onlineSessions.put(name,session);
        System.out.println(name);
        System.out.println(onlineSessions);
    }


    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        try {
            session.getBasicRemote().sendText(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClose
    public void onClose(Session session) {
        Collection<Session> col = onlineSessions.values();
        while(true == col.contains(session)) {
            col.remove(session);
        }
//       onlineSessions.remove(session.getId());
        System.out.println("close");
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 公共方法：发送信息给特定的人
     */
    public void sendMessage(String username,String jsonMsg){
        Session session=onlineSessions.get(username);
        try {
            session.getBasicRemote().sendText(jsonMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 公共方法：发送信息给所有人
     */
    public void sendMessageToAll(String jsonMsg) {
        onlineSessions.forEach((username, session) -> {
            try {
                session.getBasicRemote().sendText(jsonMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}