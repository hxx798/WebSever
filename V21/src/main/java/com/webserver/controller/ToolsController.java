package com.webserver.controller;

import com.webserver.annotations.Controller;
import com.webserver.annotations.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;

import java.io.OutputStream;
@Controller
public class ToolsController {
    public static void main(String[] args) throws Exception{
        String line = "Http://doc.canglaoshi.org";
        QRCodeUtil.encode(line,"logo.jpg","qr.jpg",true);
    }

    @RequestMapping("/createQR")
    public void createQR (HttpServletRequest request, HttpServletResponse response){
        String content = request.getParameter("content");
        response.setContentType("image/jpeg");
        OutputStream out = response.getOutputStream();
        try {
            QRCodeUtil.encode(content,out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
