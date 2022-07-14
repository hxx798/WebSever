package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例用于表示浏览器发送过来的一个HTTP的请求内容
 * 每个请求HTTP协议要求由三部分构成:请求行，消息头，消息正文
 */
public class HttpServletRequest {
    private Socket socket;

    //请求行相关信息
    private String method;  //请求方式
    private String uri;     //抽象路径
    private String protocol;//协议版本

    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();



    public HttpServletRequest(Socket socket) throws IOException {
        this.socket = socket;
        //1.1解析请求行
        parseRequestLine();
        //1.2:解析消息头
        parseHeaders();
        //1.3:解析消息正文
        parseContent();

    }

    //解析请求行
    private void parseRequestLine() throws IOException {
        String line = readLine();
        System.out.println("请求行:"+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];//这里可能出现数组下标越界，这是因为浏览器发送了空请求导致的。后期会解决。现在出现该异常先忽略。重新启动服务端重新测试。
        protocol = data[2];
    }
    //解析消息头
    private void parseHeaders() throws IOException {
        while(true) {
            String line = readLine();
            if(line.isEmpty()){//如果readLine返回空字符串，说明单独读取到了回车+换行
                break;
            }
            System.out.println("消息头:" + line);
                /*
                    将每一个消息头按照": "(冒号+空格拆)分为消息头的名字和消息头的值
                    并以key，value的形式存入到headers中
                 */
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);

        }//while循环结束，消息头解析完毕
        System.out.println("headers:"+headers);
    }
    //解析消息正文
    private void parseContent(){}

    private String readLine() throws IOException {//通常被重用的代码都不自己处理异常
        //同一个socket对象无论调用多少次getInputStream()获取的始终是同一个输入流
        InputStream in = socket.getInputStream();
        int d;//每次读取到的字节
        char cur='a',pre='a';//cur表示本次读取到的字符，pre表示上次读取到的字符
        StringBuilder builder = new StringBuilder();
        while((d = in.read())!=-1){
            cur = (char)d;
            if(pre==13&&cur==10){//是否已经连续读取到了回车+换行符
                break;
            }
            builder.append(cur);
            pre = cur;
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

    public String getHeader(String name) {
        return headers.get(name);
    }
}
