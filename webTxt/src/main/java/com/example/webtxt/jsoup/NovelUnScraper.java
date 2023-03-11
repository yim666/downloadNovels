package com.example.webtxt.jsoup;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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

/**
 * Created by Yim on 2023/3/11.
 */
public class NovelUnScraper {

    public static void main(String[] args) throws IOException {

        // 设置 Chrome Headless 选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        // 创建 ChromeDriver 对象
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));

        // 访问网页
        driver.get(Constant.novelUrl);

        // 等待页面加载完成
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // 获取页面内容
        String html = driver.getPageSource();


        // 使用 Jsoup 解析页面内容
        Document doc = Jsoup.parse(html);

        List<String> chapterUrls = new ArrayList<>();
        Elements links = doc.select(Constant.urlListReg);
        for (Element link : links) {
            chapterUrls.add(Constant.uri + link.attr("href"));
        }
        for (int a = 0; a < Constant.delNum; a++) {
            chapterUrls.remove(0);
        }
        // 逐个访问章节页面，获取内容并整合为一个txt文档
        File outputFile = new File(Constant.downLoadPath);
        FileWriter writer = new FileWriter(outputFile);
        String handle = driver.getWindowHandle();
        for (String chapterUrl : chapterUrls) {
            Document chapterDoc = null;
            int Max_Times2 = 3;
            while (Max_Times2 > 0) {

                try {
//                    // 访问网页
//                    System.out.println(driver.getWindowHandles());
//                    //打开一个新的标签页
//                    ((JavascriptExecutor) driver).executeScript("window.open();");
//
//                    Set<String> windowHandles = driver.getWindowHandles();
//                    driver.switchTo().window((String) windowHandles.toArray()[1]);
                    driver.get(chapterUrl);
//                    System.out.println(driver.getWindowHandle());

                } catch (Exception e) {
                    if (Max_Times2 == 0) break;
                    System.out.println("try Again: " + Max_Times2);
                    Max_Times2--;
                    continue;
                }

                // 获取页面内容
                String html2 = driver.getPageSource();
//                driver.close();

                // 使用 Jsoup 解析页面内容
                chapterDoc = Jsoup.parse(html2);
                //标题
                Element contentElement1 = chapterDoc.selectFirst(Constant.titleReg);
                String content1 = "<h2>" + contentElement1.text() + "</h2>";
                writer.write(content1);
                writer.write(System.lineSeparator());
                System.out.println(content1);
                //正文
                Element contentElement = chapterDoc.selectFirst(Constant.contentReg);
                String content = contentElement.text();
                writer.write(content);
                writer.write(System.lineSeparator());
                break;

            }
        }
        writer.close();
        // 关闭浏览器
        driver.quit();

        System.out.println("Novel content saved to: " + outputFile.getAbsolutePath());
    }
}
