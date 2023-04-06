package com.example.webtxt.jsoup;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by Yim on 2023/3/11.
 */
public class NovelUnScraperThread {


    private static ChromeOptions options;

    public static void main(String[] args) throws IOException {




        //把url均分
        List<List<String>> tenLists = Constant.dividArrays(getDirectoryUrls());

        // 逐个访问章节页面，获取内容并整合为一个txt文档
        File outputFile = new File(Constant.downLoadPath);
        FileWriter writer = new FileWriter(outputFile);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (List<String> urls : tenLists) {

            async1(urls, futures);

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
    public static String connectAndGetHtml(){

        // 设置小说目录页面的URL
        options = new ChromeOptions();
        // 设置 Chrome Headless 选项
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        // 创建 ChromeDriver 对象
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        //隐式等待 全局通用
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));
        // 访问网页
        driver.get(Constant.novelUrl);
        // 获取页面内容
        String html = driver.getPageSource();
        driver.quit();
        return html;
    }

    public static List<String> getDirectoryUrls(){
        // 使用 Jsoup 解析页面内容
        Document doc = Jsoup.parse(connectAndGetHtml());

        // 获取所有章节的URL
        //<a href="/books/153313/1.html">1，破笼之鸟</a>
        List<String> chapterUrls = new  ArrayList<>();
        Elements links = doc.select(Constant.urlListReg);
        for (Element link : links) {
            chapterUrls.add(Constant.uri + link.attr("href"));
        }

        for (int a = 0; a < Constant.delNum; a++) {
            chapterUrls.remove(0);
        }
        return chapterUrls;
    }

    public static void async1(List<String> urls, List<CompletableFuture<String>> futures) {
        //异步
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 创建 ChromeDriver 对象
            WebDriverManager.chromedriver().setup();
            WebDriver driver2 = new ChromeDriver(options);
            //隐式等待 全局通用
            driver2.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));
            String all = null;
            for (String chapterUrl : urls) {
                int Max_Times2 = 3;
                Document chapterDoc = null;
                String content1 = null;
                String content = null;
                while (Max_Times2 > 0) {
                    try {
                        // 访问网页
                        driver2.get(chapterUrl);
                    } catch (Exception e) {
                        if (Max_Times2 == 0) break;
                        System.out.println("try Again: " + Max_Times2);
                        Max_Times2--;
                        continue;
                    }

                    // 获取页面内容
                    String html2 = driver2.getPageSource();
                    // 使用 Jsoup 解析页面内容
                    chapterDoc = Jsoup.parse(html2);
                    //标题
                    Element contentElement1 = chapterDoc.selectFirst(Constant.titleReg);
                    content1 = "<h2>" + contentElement1.text() + "</h2>";
                    //正文
                    Element contentElement = chapterDoc.selectFirst(Constant.contentReg);
                    content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+contentElement.text();
                    break;
                }
                System.out.println(content1);
                all = all + content1 + "\n" + content + "\n";
            }
            driver2.quit();
            return all;
        });
        futures.add(future);
    }
}
