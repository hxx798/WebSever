package com.webserver.core;

import com.webserver.http.HttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理一次与客户端的HTTP交互操作
 * 处理一次HTTP交互由三步完成:
 *  * 1:解析请求
 *  * 2:处理请求
 *  * 3:发送响应
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            System.out.println(request.getMethod());


            //2处理请求

            //3发送响应
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
