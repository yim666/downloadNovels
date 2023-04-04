package com.example.webtxt.jsoup;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yim on 2023/4/2.
 */
public class Constant {
    public static String novelUrl = "https://www.ddxs.com/zhongshengcongjujueqingmeikaishi/";
    public static String uri ="https://www.ddxs.com";

    public static String  urlListReg = "td.L>a";
    public static String  titleReg = "h1";
    public static String  contentReg = "dd#contents";

    public static String downLoadPath="C:\\Users\\yimen\\mySoft\\workSpace\\test\\a.txt";

    /**
     * 删除前几个href
     */
    public static int delNum = 500;

    /**
     *  开多少线程
     */
    public static int arrayNum = 12;


    public static List<List<String>> dividArrays(List<String> list){
        int sum=list.size();
        int avg =sum /arrayNum;
        int suf =sum % arrayNum;
        List<List<String>> collect = IntStream.range(0, Constant.arrayNum)
                .mapToObj(i -> {
                    if(i < suf) {
                        return list.subList(i * (avg+1),  (i + 1) * (avg + 1));
                    }else{
                        return  list.subList(sum-(Constant.arrayNum-i)*avg, sum-(Constant.arrayNum-i)*avg+avg);
                    }

                })
                .collect(Collectors.toList());
        return collect;
    }
}
