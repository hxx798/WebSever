package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

public class DispatcherServlet {
    private static DispatcherServlet servlet;
    private static File rootDir;
    private static File staticDir;

    static {
        //定位到：target/classes
        try {
            rootDir = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //定位到static目录
        staticDir = new File(rootDir,"static");
    }

    private DispatcherServlet(){}

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws URISyntaxException {

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
    }
    public static DispatcherServlet getInstance(){
        return servlet;
    }
}
