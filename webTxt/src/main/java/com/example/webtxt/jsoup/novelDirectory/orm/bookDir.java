package com.example.webtxt.jsoup.novelDirectory.orm;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Yim on 2023/4/9.
 */
@Data
@Accessors(chain = true)
public class bookDir {
    private String bookname;
    private String url ;
    private int num;
    private String info;
}
