package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpServletRequest {
    private Socket socket;

    private String method;  //请求方式
    private String uri;     //抽象路径
    private String protocol;//协议版本

    private String requestURI;//保存uri中的请求部分(?左侧的部分)
    private String queryString;//保存uri的参数部分(?右侧的内容)
    private Map<String, String> parameters = new HashMap<>();//保存每一组参数

    private Map<String, String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1.1解析请求行
        parseRequestLine();
        //1.2解析消息头
        parseHeaders();
        //1.3解析消息征文
        parseContent();

    }

    //解析请求行
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readline();

        if(line.isEmpty()){//如果请求行为空字符串,则说明本次为空请求
            throw new EmptyRequestException();
        }
        System.out.println("请求行:"+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        protocol = data[2];
        parseURI();
    }

    //进一步解析uri
    private void parseURI(){
        String[] uri_ = uri.split("\\?");
        requestURI = uri_[0];
        if (uri.contains("?")) {
            queryString = uri_[1];
            parseParameters(queryString);
            System.out.println(parameters);
        }
    }

    //解析参数
    private void parseParameters(String line){
        try {
            line = URLDecoder.decode(line,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String[] queryString_ = line.split("&");
        for (String s : queryString_) {
            String[] paras = s.split("=");
            parameters.put(paras[0], paras.length > 1 ? paras[1] : "");
        }
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
                //消息头无论大小写，全部转小写存入headers，保证兼容性
                headers.put(data[0].toLowerCase(Locale.ROOT),data[1]);

            }//while循环结束，消息头分析完毕
//            System.out.println("headers:"+headers);
    }

    //解析消息正文
    private void parseContent() throws IOException {
        //判读本次是否为post请求
        if ("POST".equalsIgnoreCase(method)){//ignore忽略大小写判断post
            //根据消息头Content-Length
            String contentLength = getHeader("Content-Length");
            if (contentLength!=null){//u确保有消息头Content-Length
                int length = Integer.parseInt(contentLength);
                System.out.println("正文长度："+length);
                byte[] data = new byte[length];
                InputStream inputStream = socket.getInputStream();
                inputStream.read(data);
                //根据ContentType判断正文类型，进行对应处理
                String contentType = getHeader("Content-Type");
                //分支判断不同类型不同处理
                if ("application/x-www-form-urlencoded".equals(contentType)){
                    String line = new String(data, StandardCharsets.ISO_8859_1);
                    System.out.println("正文内容"+line);
                    parseParameters(line);
                }
//                else if () {} //拓展其他类型进行处理
            }
        }
    }

    private String readline() throws IOException {//需要被复用的方法自己不处理异常
        //同一个socket对象无论调用多少次getInputStream()获取的始终是同一个输入流
        InputStream inputStream = socket.getInputStream();
        int d;
        char cur,pre='a';//cur表示本次读取到的字符，pre表示上次读取到的字符
        StringBuilder builder = new StringBuilder();
        while ((d = inputStream.read())!=-1){
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

    public String getHeader(String name) {
        /*
            headers:
            key             value
            content-type    xxx/xxx
         */
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }


}

