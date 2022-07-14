package com.webserver.core;

import com.webserver.http.HttpServletRequest;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
//            System.out.println(request.getMethod());//GET

            //2处理请求

            //3发送响应
            /*
                MAVEN项目的结构特点:
                src/main/java下存放的是项目中所有的源代码。只有.java文件才能放在这里
                src/main/resources下存放的是项目中所用到的所有资源文件(非.java文件都算资源文件)
                当MAVEN项目编译后，会生成target/classes目录，并将java和resources中的内容都合并
                放到target/classes目录中。
                而JVM运行起来后执行的都是target/classes目录中的内容，因此该目录可以理解为是我们
                项目的跟目录。
                若想定位这个目录，可以使用:
                类名.class.getClassLoader.getResources(".")
                这里的类名指的是在哪个类中需要定位这个目录，就写这个类名即可。

                测试将target/classes/static目录下的index.html页面给浏览器发送回去
             */
            //定位到：target/classes
            File rootDir = new File(
            ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            //定位到static目录
            File staticDir = new File(rootDir,"static");
            //定位到index.html
//            File file = new File(staticDir,"index.html");
            //根据浏览器发送的请求中的抽象路径部分，去static目录下寻找页面
            String path = request.getUri();
            File file = new File(staticDir,path);
            System.out.println("该页面是否存在:"+file.exists());


            /*
                通过socket获取输出流给浏览器发送一个标准的HTTP响应，并在响应中包含index页面
                内容让浏览器接收后呈现出来。
                响应内容:
                HTTP/1.1 200 OK(CRLF)
                Content-Type: text/html(CRLF)
                Content-Length: 2546(CRLF)(CRLF)
                1011101010101010101......(index.html页面的所有字节)
            */
            OutputStream outputStream = socket.getOutputStream();
            //3.1发送状态行
            String line = "HTTP/1.1 200 OK";
            outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
            outputStream.write(13);//回车
            outputStream.write(10);//换行

            //3.2发送响应头
            line = "Content-Type: text/html";
            outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
            outputStream.write(13);//发送回车符
            outputStream.write(10);//发送换行符
            line = "Content-Length"+file.length();
            outputStream.write(line.getBytes(StandardCharsets.ISO_8859_1));
            outputStream.write(13);//回车
            outputStream.write(10);//换行
            //单独发送回车换行表示响应头发完了
            outputStream.write(13);//回车
            outputStream.write(10);//换行

            //3.3发送相应正文(index.html里的所有字节)
            FileInputStream fileInputStream = new FileInputStream(file);
            int length;
            byte[] data = new byte[1024*10];
            while ((length=fileInputStream.read(data))!=-1){
                outputStream.write(data,0,length);
            }

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
