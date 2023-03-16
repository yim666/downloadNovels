package com.example.webtxt.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yim on 2023/3/11.
 */
public class NovelScraper {

    private static int  Max_Times = 5;
//    private static int  Max_Times2 = 3;
    public static void main(String[] args) throws IOException {

        // 设置小说目录页面的URL
        String novelUrl = "http://www.bookshuku.com/read/133668.html";

        // 获取小说目录页面的HTML内容
//        Document doc = Jsoup.connect(novelUrl).get();
        Document doc=null;
        while (Max_Times>0){
            try {

                 doc = Jsoup.connect(novelUrl)
                        .timeout(5000)      // 设置连接超时时间为 10 秒
                        .maxBodySize(0)      // 不限制 HTML 页面大小
                        .followRedirects(true)  // 自动重定向
                        .userAgent("Mozilla/5.0")  // 设置浏览器标识
                        .header("Accept-Encoding", "gzip, deflate")  // 设置请求头
                        .ignoreContentType(true)  // 忽略内容类型
                        .ignoreHttpErrors(true)   // 忽略 HTTP 错误
                        .get();
                 break;
            }catch (Exception e){
                System.out.println("还有"+Max_Times+"次重连");
                Max_Times --;
            }
            if(Max_Times == 0) throw new IOException();
        }



        // 获取所有章节的URL
//        <a href="http://www.bookshuku.com/read/133668_2.html"
//        <a href="/html/124950/1.html">第1章 来自异形的科技（求收藏，追读，推荐）</a>
        List<String> chapterUrls = new ArrayList<>();
        Elements links = doc.select("a[href^=http://www.bookshuku.com/read/]");
        for (Element link : links) {
            chapterUrls.add(link.absUrl("href"));
        }

        // 逐个访问章节页面，获取内容并整合为一个txt文档
        File outputFile = new File("novel.txt");
        FileWriter writer = new FileWriter(outputFile);
        for (String chapterUrl : chapterUrls) {
            Document chapterDoc = null;
            int  Max_Times2 = 3;
            while (Max_Times2>0){
                try {
                     chapterDoc = Jsoup.connect(chapterUrl)
                            .timeout(5000)      // 设置连接超时时间为 10 秒
                            .maxBodySize(0)      // 不限制 HTML 页面大小
                            .followRedirects(true)  // 自动重定向
                            .userAgent("Mozilla/5.0")  // 设置浏览器标识
                            .header("Accept-Encoding", "gzip, deflate")  // 设置请求头
                            .ignoreContentType(true)  // 忽略内容类型
                            .ignoreHttpErrors(true)   // 忽略 HTTP 错误
                            .validateTLSCertificates(false)  // 不验证 SSL 证书
                            .get();
                     break;
                }catch (Exception e){
                    if(Max_Times2 == 0) break;
                    Max_Times2 --;
                }
            }
            if(Max_Times2 == 0) continue;

            //标题
            Element contentElement1 = chapterDoc.selectFirst("div.view_t");
            String content1 = contentElement1.text();
            writer.write(content1);
            writer.write(System.lineSeparator());

            //正文
            Element contentElement = chapterDoc.selectFirst("div#view_content_txt");
            String content = contentElement.text();
            writer.write(content);
            writer.write(System.lineSeparator());
        }
        writer.close();

        System.out.println("Novel content saved to: " + outputFile.getAbsolutePath());
    }
}
