package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
            //1.1解析请求行
            //请求行相关信息
            String line = readline();

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

            //1.2解析消息头
            Map<String, String> headers = new HashMap<>();
            while (true) {
                line = readline();
                if (line.isEmpty()) {
                    break;
                }
                String[] header = line.split(":\\s");
                headers.put(header[0],header[1]);
//                System.out.println("消息头:"+line);
            }
            System.out.println(headers);

            //2处理请求

            //3发送响应
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *复用代码写成方法
     */
    private String readline() throws IOException {//需要被复用的方法自己不处理异常
        InputStream inputStream = socket.getInputStream();
        int d ;
        char cur,pre='a';
        StringBuilder builder = new StringBuilder();
        while ((d=inputStream.read())!=-1){
            cur = (char)d;
            if(pre==13&&cur==10){
                break;
            }
            builder.append(cur);
            pre=cur;
        }
        return builder.toString().trim();
    }
}
