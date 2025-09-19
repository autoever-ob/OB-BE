package com.campick.server.common.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class MemberHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        System.out.println("inside beforeHandshake");

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String path = servletRequest.getServletRequest().getRequestURI();
            System.out.println("path: " + path);
            if (path.matches("/ws/\\w+")) {
                Long userId = Long.valueOf(path.replace("/ws/", ""));
                System.out.println(userId);
                attributes.put("userId", userId);
            } else {
                // 형식이 안 맞으면 연결 거부
                System.out.println("false");
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {}
}

