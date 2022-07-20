package com.webserver.core;

import com.webserver.annotations.Controller;
import com.webserver.annotations.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public class DispatcherServlet {
    private static DispatcherServlet servlet;
    private static File rootDir;
    private static File staticDir;

    private static File dir;

    static {
        servlet = new DispatcherServlet();
        try {
            //定位到：target/classes
            rootDir = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );
            dir = new File(rootDir,"com/webserver/controller");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //定位到static目录
        staticDir = new File(rootDir,"static");
    }

    private DispatcherServlet(){}

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws URISyntaxException, IOException {

        //根据浏览器发送的请求中的抽象路径部分，去static目录下寻找页面
        String path = httpServletRequest.getRequestURI();

        //判断本次请求是否为某个业务
//        if ("/regUser".equals(path)) {
//            UserController controller = new UserController();
//            controller.reg(httpServletRequest, httpServletResponse);
//        } else if ("/loginUser".equals(path)) {
//            UserController controller = new UserController();
//            controller.login(httpServletRequest, httpServletResponse);
//        } else if ("/userList".equals(path)) {
//            UserController controller = new UserController();
//            controller.userList(httpServletRequest, httpServletResponse);
//        } else if ("/writeArticle".equals(path)) {
//            ArticleController controller = new ArticleController();
//            controller.writeArticle(httpServletRequest, httpServletResponse);
//        }
//        else if("/showAllArticle".equals(path)){
//            ArticleController controller = new ArticleController();
//            controller.articleList(httpServletRequest,httpServletResponse);
//        } else if ("/createQR".equals(path)) {
//            ToolsController controller = new ToolsController();
//            controller.createQR(httpServletRequest,httpServletResponse);
        if (true){
            File[] classFiles = dir.listFiles(f->f.getName().endsWith(".class"));
            for (File file:classFiles
                 ) {
                String fileName = file.getName();
                String className = fileName.substring(0,fileName.indexOf("."));
                try {
                    Class cls = Class.forName("com.webserver.controller."+className);
                    if (cls.isAnnotationPresent(Controller.class)){
                        Object obj = cls.newInstance();
                        Method[] methods = cls.getDeclaredMethods();
                        for (Method method:methods
                        ) {
                            if (method.isAnnotationPresent(RequestMapping.class)){
                                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                                String value = requestMapping.value();
                                if (value.equals(path)){
                                    method.invoke(obj,httpServletRequest,httpServletResponse);
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

            }
        } else{
            File file = new File(staticDir, path);
            System.out.println("该页面是否存在:" + file.exists());
            if (file.isFile()) {//用户请求的资源在static目录下存在且是一个文件
                httpServletResponse.setContentFile(file);
            } else {
                httpServletResponse.setStatusCode(404);
                httpServletResponse.setStatusReason("NotFound");
                httpServletResponse.setContentFile(new File(staticDir, "/root/404.html"));
            }
        }
        //测试添加其他头
        httpServletResponse.addHeader("Server", "WebSever");
    }
    public static DispatcherServlet getInstance(){
        return servlet;
    }
}
