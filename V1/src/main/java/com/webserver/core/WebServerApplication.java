package com.webserver.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServerApplication {
    private ServerSocket serverSocket;
    public WebServerApplication() {
        try {
            System.out.println("正在启动服务端");
            serverSocket=new ServerSocket(8080);
            System.out.println("服务端启动完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        try {
            System.out.println("等待和苦短链接");
            Socket socket=serverSocket.accept();
            System.out.println("一个客户端已连接");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        WebServerApplication webServerApplication=new WebServerApplication();
        webServerApplication.start();
    }
}