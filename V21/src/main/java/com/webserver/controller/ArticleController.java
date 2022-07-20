package com.webserver.controller;

import com.webserver.annotations.Controller;
import com.webserver.annotations.RequestMapping;
import com.webserver.entity.Article;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Controller
public class ArticleController {
    private static File articleDir;
    static {
        articleDir = new File("./articles");
        if (!articleDir.exists()) {
            articleDir.mkdirs();
        }
    }

    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理发表文章...");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");

        if (title == null || author == null || content == null || title.isEmpty() || author.isEmpty() || content.isEmpty()) {
            response.sendRedirect("/article_info_error.html");
            return;
        }

        File articleFile = new File(articleDir, title + ".obj");
        if (articleFile.exists()) {
            response.sendRedirect("/have_article.html");
            return;
        }

        Article article = new Article(title, author, content);

        try (
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
        ) {
            objectOutputStream.writeObject(article);
            response.sendRedirect("/article_success.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/showAllArticle")
    public void articleList(HttpServletRequest request, HttpServletResponse response) {
        List<Article> articleList = new ArrayList<>();

        File[] subs = articleDir.listFiles(f -> f.getName().endsWith(".obj"));
        for (File sub : subs) {
            try (
                    FileInputStream fis = new FileInputStream(sub);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                Article article = (Article) ois.readObject();
                articleList.add(article);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        response.setContentType("text/html;charset=utf-8");

        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>文章列表</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<center>");
        pw.println("<h1>文章列表</h1>");
        pw.println("<table border=\"1\">");
        pw.println("<tr>");
        pw.println("<td>标题</td>");
        pw.println("<td>作者</td>");
        pw.println("</tr>");

        for (Article article : articleList) {
            pw.println("<tr>");
            pw.println("<td>" + article.getTitle() + "</td>");
            pw.println("<td>" + article.getAuthor() + "</td>");
            pw.println("</tr>");
        }

        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");


    }
}


