package com.example.webtxt.jsoup;

import io.netty.util.internal.StringUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.net.HttpHeaders.ReferrerPolicyValues.STRICT_ORIGIN_WHEN_CROSS_ORIGIN;

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
        Map<String, String> headers =new HashMap();
//        headers.put()                \": \"
        //                ": " 
headers.put("sec-ch-ua-platform","\"Windows\"");
headers.put("sec-ch-ua","\"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"");
            String input =
                    "               \"authority: www.uuks.org\" " +
                    "               \"accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\" " +
                    "               \"accept-language: zh-CN,zh;q=0.9\" " +
                    "               \"cache-control: max-age=0\" " +
                    "               \"cookie: zh_choose=t\" " +
                    "               \"if-modified-since: Mon, 03 Apr 2023 15:30:37 GMT\" " +
                    "               \"sec-ch-ua-mobile: ?0\" " +
                    "              \"sec-fetch-dest: document\" " +
                    "              \"sec-fetch-mode: navigate\" " +
                    "               \"sec-fetch-site: none\" " +
                    "               \"sec-fetch-user: ?1\" " +
                    "               \"upgrade-insecure-requests: 1\" " +
                    "               \"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36\" " +
                    "             ";
        List<String> matches = extractQuotedStrings(input);
        for (String a:matches){
            String[] split = a.split(":");
            headers.put(split[0],split[1]);
        }
        System.out.println(matches);
        Document doc=null;
        while (Max_Times>0){
            try {
                Connection.Response res = Jsoup.connect(novelUrl)
//                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .headers(headers)
                        .referrer(STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                        .timeout(20*1000)
                        .execute();
                System.out.println(res.statusCode()+"--->"+res.statusMessage());

                doc = Jsoup.connect(novelUrl)
                        .timeout(6000)
//                        .cookies(cookies)
                        .ignoreHttpErrors(true)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")
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
//        Elements links = doc.select("a[href=/zhongshengcongjujueqingmeikaishi]");
        Elements links = doc.select("a[href=/b/64170]");
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

    public static List<String> extractQuotedStrings(String input) {
        List<String> matches = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }
}
