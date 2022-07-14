package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpServletResponse {
    private Socket socket;

    //状态行的相关信息
    private int statusCode = 200;           //状态代码
    private String statusReason = "OK";     //状态描述

    //响应头相关信息
    private Map<String,String> headers = new HashMap<>();   //响应头

    //响应正文相关信息
    private File contentFile;               //正文对应的实体文件

    public HttpServletResponse(Socket socket) {this.socket = socket;}

    /**
     * 将当前响应对象内容以标准的HTTP响应格式，发送给客户端(浏览器)
     */
    public void response() throws IOException {
        //3.1发送状态行
        sendStatusLine();
        //3.2发送响应头
        sendHeaders();
        //3.3发送相应正文(index.html里的所有字节)
        sendContent();
    }

    //发送状态行
    private void sendStatusLine() throws IOException {
        println("HTTP/1.1"+" "+statusCode+" "+statusReason);
    }

    //发送响应头
    private void sendHeaders() throws IOException {
        Set<Map.Entry<String,String>> entrySet = headers.entrySet();
        for(Map.Entry<String,String> e : entrySet){
            String name = e.getKey();
            String value = e.getValue();
            println(name + ": " + value);
        }

        //单独发送一组回车+换行表示响应头部分发送完了!
        println("");
    }

    //发送响应正文
    private void sendContent() throws IOException {
        try (
                FileInputStream fileInputStream = new FileInputStream(contentFile)
                ){
            OutputStream outputStream = socket.getOutputStream();
            int length;
            byte[] data = new byte[1024*10];
            while ((length=fileInputStream.read(data))!=-1){
                outputStream.write(data,0,length);
            }
        }
    }

    /**
     * 向浏览器发送一行字符串(自动补充CR+LF)
     */
    private void println(String line) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
        outputStream.write(13);//发送回车符
        outputStream.write(10);//发送换行符
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
    }

    public void addHeader(String name, String value){
        headers.put(name,value);
    }
}
