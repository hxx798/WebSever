package com.webserver.controller;

import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前类负责处理与用户相关的操作
 */
public class UserController {
    private static File userDir;//该目录用于保存所有注册用户文件（obj文件）

    static {
        userDir = new File("./users");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理用户注册操作...");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String age = request.getParameter("age");
        System.out.println(username + "," + password + "," + nickname + "," + age);

        //验证数据信息正确性
        if (username.isEmpty() || password.isEmpty() || nickname.isEmpty() || age.isEmpty() || !age.matches("[0-9]+")) {
            //出现上述情况，响应错误
            response.sendRedirect("/reg_info_error.html");
            return;
        }
        //正确则处理
        //先年龄字符串转int
        int age_ = Integer.parseInt(age);
        User user = new User(username, password, nickname, age_);

        File userFile = new File(userDir, username + ".obj");

        if (userFile.exists()) {//文件已经存在说明该用户已经存在了!
            response.sendRedirect("/have_user.html");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(userFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos)
        ) {
            objectOutputStream.writeObject(user);
            response.sendRedirect("/reg_success.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void userList(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("开始处理动态页面...");
        //将users目录下所有的obj文件反序列化后将得到的User对象存入集合userList
        List<User> userList = new ArrayList<>();
        /*
            将users目录下的所有obj文件获取回来，并进行反序列化操作，得到所有的User对象
            并存入到userList集合备用
         */
        File[] subs = userDir.listFiles(f -> f.getName().endsWith(".obj"));
        for (int i = 0; i < subs.length; i++) {
            try (
                    FileInputStream fis = new FileInputStream(subs[i]);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User) ois.readObject();
                System.out.println(user);
                userList.add(user);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        response.setContentType("text/html;charset=utf-8");

        PrintWriter pw = response.getWriter();

        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>用户列表</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<center>");
        pw.println("<h1>用户列表</h1>");
        pw.println("<table border=\"1\">");
        pw.println("<tr>");
        pw.println("<td>用户名</td>");
        pw.println("<td>密码</td>");
        pw.println("<td>昵称</td>");
        pw.println("<td>年龄</td>");
        pw.println("<td>操作</td>");
        pw.println("</tr>");

        for (User user : userList) {
            pw.println("<tr>");
            pw.println("<td>" + user.getUsername() + "</td>");
            pw.println("<td>" + user.getPassword() + "</td>");
            pw.println("<td>" + user.getNickname() + "</td>");
            pw.println("<td>" + user.getAge() + "</td>");
            // <td><a href='/deleteUser?username=xxxxx'>删除</a></td>
            pw.println("<td><a href='/deleteUser?username=" + user.getUsername() + "'>删除</a></td>");
            pw.println("</tr>");
        }

        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");



    }

    public void login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("开始处理登陆...");

        File userFile = new File(userDir, username + ".obj");
        if (userFile.exists()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(userFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
            ) {
                User user = (User) objectInputStream.readObject();
                String password_ = user.getPassword();
                if (password.equals(password_)) {
                    response.sendRedirect("login_success.html");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        response.sendRedirect("login_fail.html");

    }

//    public void delete(HttpServletRequest request,HttpServletResponse response){
//        String username = request.getParameter("username");
//        System.out.println("要删除的用户是:"+username);
//        //根据用户名定位users目录下对应的obj文件，将该文件删除
//        File userFile = new File(userDir,username+".obj");
//        if(userFile.exists()){
//            userFile.delete();
//        }
//        response.sendRedirect("/userList");
//    }
}
