package com.example.webtxt.jsoup.novelDirectory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yim on 2023/4/2.
 */
public class DirConstant {
    public static String novelUrl = "https://book.qq.com/book-cate/0-0-1-0-3-2-4-";
    public static String uri ="https://www.ddxs.com";

    public static String  urlListReg = "div.rank-book>a";
    public static String  titleReg = "h1";
    public static String  contentReg = "dd#contents";


    /**
     * 删除前几个href
     */
    public static int delNum = 100;

    /**
     *  开多少线程
     */
    public static int arrayNum = 12;


    public static List<List<String>> dividArrays(List<String> list){
        int sum=list.size();
        int avg =sum /arrayNum;
        int suf =sum % arrayNum;
        List<List<String>> collect = IntStream.range(0, DirConstant.arrayNum)
                .mapToObj(i -> {
                    if(i < suf) {
                        return list.subList(i * (avg+1),  (i + 1) * (avg + 1));
                    }else{
                        return  list.subList(sum-(DirConstant.arrayNum-i)*avg, sum-(DirConstant.arrayNum-i)*avg+avg);
                    }

                })
                .collect(Collectors.toList());
        return collect;
    }
}
