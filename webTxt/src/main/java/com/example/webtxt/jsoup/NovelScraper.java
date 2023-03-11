package com.example.webtxt.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yim on 2023/3/11.
 */
public class NovelScraper {

    private static int  Max_Times = 3;
//    private static int  Max_Times2 = 3;
    public static void main(String[] args) throws IOException {

        // 设置小说目录页面的URL
//        String novelUrl = "https://www.ddxs.com/zhongshengcongjujueqingmeikaishi/";
        String novelUrl = "https://www.uuks.org/b/64170/";


        Document doc=null;
        while (Max_Times>0){
            try {
//                Connection.Response res = Jsoup.connect(novelUrl)
//                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")  // 设置浏览器标识
//                        .timeout(20*1000)
//                        .execute();
                Map<String, String> cookies =new HashMap();
                cookies.put("read_setting", "%257B%2522bgColor%2522%253A%2520%2522%2523E9EEF2%2522%252C%2522fontSize%2522%253A%2520%252220px%2522%252C%2522fontFamily%2522%253A%2520%2522Microsoft%2520Yahei%2522%257D");
                cookies.put("zh_choose", "t");
                doc = Jsoup.connect(novelUrl)
                        .timeout(6000)
                        .cookies(cookies)
                        .ignoreHttpErrors(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")  // 设置浏览器标识
                        .get();
                break;
            }catch (Exception e){
                System.out.println("还有"+Max_Times+"次重连");
                Max_Times --;
            }
            if(Max_Times == 0) throw new IOException();
        }



        // 获取所有章节的URL
        //<a href="/books/153313/1.html">1，破笼之鸟</a>
        //href="/txt/134898/1.html"
        //<a href="/zhongshengcongjujueqingmeikaishi/1.html">第一章 夏洛特烦恼</a>
        List<String> chapterUrls = new ArrayList<>();
//        Elements links = doc.select("a[href^=/zhongshengcongjujueqingmeikaishi]");
        Elements links = doc.select("a[href^=/b/64170]");
        for (Element link : links) {
            chapterUrls.add(link.absUrl("href"));
        }
        chapterUrls.remove(0);

        // 逐个访问章节页面，获取内容并整合为一个txt文档
        File outputFile = new File("D:\\workspace\\projects\\downloadNovel\\showweb\\public\\txtcontent\\novel.txt");
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
            Element contentElement1 = chapterDoc.selectFirst("h1");
            String content1 = contentElement1.text();
            writer.write(content1);
            writer.write(System.lineSeparator());
            System.out.println(content1);
            //正文
            Element contentElement = chapterDoc.selectFirst("dd#contents");
            String content = contentElement.text();
            writer.write(content);
            writer.write(System.lineSeparator());
            break;
        }
        writer.close();

        System.out.println("Novel content saved to: " + outputFile.getAbsolutePath());
    }
}
