package com.example.webtxt.jsoup;

/**
 * Created by Yim on 2023/4/2.
 */
public class Constant {
    public static String novelUrl = "https://www.ddxs.com/zhongshengcongjujueqingmeikaishi/";
    public static String uri ="https://www.ddxs.com";

    public static String  urlListReg = "td.L>a";
    public static String  titleReg = "h1";
    public static String  contentReg = "dd#contents";

    public static String downLoadPath="D:\\workspace\\projects\\downloadNovel\\showweb\\public\\txtcontent\\novel.txt";

    /**
     * 删除前几个href
     */
    public static int delNum = 15;

    /**
     *  开多少线程
     */
    public static int arrayNum = 10;
}
