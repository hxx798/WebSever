package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

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

            //2处理请求
            DispatcherServlet.getInstance().service(httpServletRequest,httpServletResponse);

            //3.发送响应
            httpServletResponse.response();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e) {

        } finally {
            //按照HTTP协议要求，一次交互后断开TCP链接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
