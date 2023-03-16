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
import java.util.concurrent.CompletableFuture;

/**
 * Created by Yim on 2023/3/11.
 */
public class NovelScraperAsync {

    private static int  Max_Times = 9;
    public static void main(String[] args) throws IOException {

        // 设置小说目录页面的URL
        String novelUrl = "http://www.bookshuku.com/read/133668.html";


        Document doc=null;
        while (Max_Times>0){
            try {

                 doc = Jsoup.connect(novelUrl)
                        .timeout(5000)      // 设置连接超时时间为 10 秒
                        .maxBodySize(0)      // 不限制 HTML 页面大小
                        .followRedirects(true)  // 自动重定向
//                        .userAgent("Mozilla/5.0")  // 设置浏览器标识
//                        .header("Accept-Encoding", "gzip, deflate")  // 设置请求头
//                        .ignoreContentType(true)  // 忽略内容类型
//                        .ignoreHttpErrors(true)   // 忽略 HTTP 错误
                        .get();
                 break;
            }catch (Exception e){
                System.out.println("还有"+Max_Times+"次重连");
                Max_Times --;
            }
            if(Max_Times == 0) throw new IOException();
        }



        // 获取所有章节的URL
//        <a href="/html/124950/1.html">第1章 来自异形的科技（求收藏，追读，推荐）</a>
        List<String> chapterUrls = new ArrayList<>();
        Elements links = doc.select("a[href^=http://www.bookshuku.com/read/]");
        for (Element link : links) {
            chapterUrls.add(link.absUrl("href"));
        }

        // 逐个访问章节页面，获取内容并整合为一个txt文档
        File outputFile = new File("novel2.txt");
        FileWriter writer = new FileWriter(outputFile);
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (String chapterUrl : chapterUrls) {
            //异步
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                int Max_Times2 = 3;
                Document chapterDoc = null;
                String content1 = null;
                String content = null;
                while (Max_Times2 > 0) {
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
                        //标题
                        Element contentElement1 = chapterDoc.selectFirst("div.view_t");
                        content1 = contentElement1.text();

                        //正文
                        Element contentElement = chapterDoc.selectFirst("div#view_content_txt");
                        content = contentElement.text();
                        break;
                    } catch (Exception e) {
                        if (Max_Times2 == 0) break;
                        Max_Times2--;
                    }
                }
                System.out.println("异步完成+1");
                return content1 + "\n" + content+"\n";

            });
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<String> combinedFuture = allFutures.thenApply(v -> {
            StringBuilder sb = new StringBuilder();
            for (CompletableFuture<String> future : futures) {
                sb.append(future.join()).append("\n");
            }
            return sb.toString();
        });
        String result = combinedFuture.join();
        writer.write(result);
        writer.write(System.lineSeparator());
        writer.close();

        System.out.println("Novel content saved to: " + outputFile.getAbsolutePath());
    }
}
