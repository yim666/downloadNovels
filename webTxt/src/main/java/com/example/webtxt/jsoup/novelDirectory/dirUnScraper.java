package com.example.webtxt.jsoup.novelDirectory;

import com.example.webtxt.jsoup.novelDirectory.orm.bookDir;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;

/**
 * Created by Yim on 2023/3/11.
 */
public class dirUnScraper {

    public static void main(String[] args) throws IOException, SQLException {

        // 设置 Chrome Headless 选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        // 创建 ChromeDriver 对象
        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));


        JdbcExample<bookDir> jdbcExample = new JdbcExample<>();

        ArrayList<bookDir> bookDirs = new ArrayList<>();
        for (int a=1;a<=50;a++){

            // 访问网页
            driver.get(DirConstant.novelUrl+a);
            // 获取页面内容
            String html = driver.getPageSource();
            // 使用 Jsoup 解析页面内容
            Document doc = Jsoup.parse(html);
            Elements links = doc.select(DirConstant.urlListReg);
            for (Element link : links) {
                String title=link.attr("title");
                String url ="https:"+link.attr("href");
                Element element = link.selectFirst("div>p");
                String info = element.text();

                bookDir bookDir = new bookDir();
                bookDir.setBookname(title).setUrl(url).setInfo(info).setNum(100);
                bookDirs.add(bookDir);


            }
        }
        jdbcExample.insertList(bookDirs);


        // 关闭浏览器
        driver.quit();

    }
}
