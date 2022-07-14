package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {
    private Socket socket;

    private String method;  //请求方式
    private String uri;     //抽象路径
    private String protocol;//协议版本

    private Map<String, String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        //1.1解析请求行
        parseRequestLine();
        //1.2解析消息头
        parseHeaders();
        //1.3解析消息征文
        parseContent();

    }

    //解析请求行
    private void parseRequestLine() throws IOException {
        String line = readline();
//        System.out.println("请求行:"+line);

        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];

    }

    //解析消息头
    private void parseHeaders() throws IOException {
            while (true) {
                String line = readline();
                if (line.isEmpty()) {//如果readLine返回空字符串，说明单独读取到了回车+换行
                    break;
                }
                System.out.println("消息头:" + line);
                /*
                  将每一个消息头按照": "(冒号+空格拆)分为消息头的名字和消息头的值并以key，value的形式存入到headers中
                */
                String[] data = line.split(":\\s");
                headers.put(data[0],data[1]);

            }//while循环结束，消息头分析完毕
//            System.out.println("headers:"+headers);
    }

    //解析消息正文
    private void parseContent(){

    }

    private String readline() throws IOException {//需要被复用的方法自己不处理异常
        //同一个socket对象无论调用多少次getInputStream()获取的始终是同一个输入流
        InputStream inputStream = socket.getInputStream();
        int d ;
        char cur,pre='a';//cur表示本次读取到的字符，pre表示上次读取到的字符
        StringBuilder builder = new StringBuilder();
        while ((d=inputStream.read())!=-1){
            cur = (char)d;
            if(pre==13&&cur==10){//是否已经连续读取到了回车+换行符
                break;
            }
            builder.append(cur);
            pre=cur;
        }
        return builder.toString().trim();
    }


    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeaders(String name) {
        return headers.get(name);
    }
}

