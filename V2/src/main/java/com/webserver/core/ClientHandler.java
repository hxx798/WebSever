package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 处理一次与客户端的HTTP交互操作
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
            InputStream inputStream = socket.getInputStream();
            int d ;
            while ((d=inputStream.read())!=-1){
                System.out.println((char) d);
            }

            //2处理请求

            //3发送响应
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
