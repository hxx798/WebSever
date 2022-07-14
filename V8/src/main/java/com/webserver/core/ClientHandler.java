package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;

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
            HttpServletRequest httpServletRequest = new HttpServletRequest(socket);
            HttpServletResponse httpServletResponse = new HttpServletResponse(socket);
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

            //根据浏览器发送的请求中的抽象路径部分，去static目录下寻找页面
            String path = httpServletRequest.getUri();
            File file = new File(staticDir,path);
            System.out.println("该页面是否存在:"+file.exists());

            if(file.isFile()){//用户请求的资源在static目录下存在且是一个文件
                httpServletResponse.setContentFile(file);
            }else{
                httpServletResponse.setStatusCode(404);
                httpServletResponse.setStatusReason("NotFound");
                httpServletResponse.setContentFile(new File(staticDir,"/root/404.html"));
            }

            //3.发送响应
            httpServletResponse.response();

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }finally {
            //按照HTTP协议要求，一次交互后断开TCP链接
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }




}
