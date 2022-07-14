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
            char cur='a',pre='a';
            StringBuilder builder = new StringBuilder();
            while ((d=inputStream.read())!=-1){
                cur = (char)d;
                if(pre==13&&cur==10){
                    break;
                }
                builder.append(cur);
                pre=cur;
            }
            String line = builder.toString().trim();
            System.out.println(line);
            //请求行相关信息
            String method;  //请求方式
            String uri;     //抽象路径
            String protocol;//协议版本

            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];

            //测试路径:http://localhost:8088/index.html
            System.out.println("method:"+method);//method:GET
            System.out.println("uri:"+uri);//uri:/index.html
            System.out.println("protocol:"+protocol);//protocol:HTTP/1.1

            //2处理请求

            //3发送响应
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
