package com.example.webtxt.jsoup;

import com.example.webtxt.jsoup.novelDirectory.orm.bookDir;

import java.lang.reflect.Field;

/**
 * Created by Yim on 2023/4/4.
 */
public class mainSH {
    public static void main(String[] args) throws IllegalAccessException {
        bookDir b = new bookDir();
        b.setBookname("aaa");
        String name = bookDir.class.getName();
        String name1 = b.getClass().getSimpleName();

        for(Field f:b.getClass().getDeclaredFields()){
            f.setAccessible(true);
            if(f.get(b) instanceof String){
                System.out.println(f.getName());
            }
        }
    }
}
