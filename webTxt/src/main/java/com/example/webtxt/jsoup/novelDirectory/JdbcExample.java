package com.example.webtxt.jsoup.novelDirectory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

public class JdbcExample<T> {
    // JDBC 驱动和数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://150.158.50.97:3306/test";

    // 数据库用户名和密码
    static final String USER = "yim";
    static final String PASS = "Yimmm@666";
    Connection conn = null;
    Statement stmt = null;

    //建立连接
    public JdbcExample(){
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开连接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);


//            String sql = "INSERT INTO user (name, age) VALUES ('Alice', 25)";
//            stmt.executeUpdate(sql);
//
//            // 更新数据
//            sql = "UPDATE user SET age = 30 WHERE name = 'Alice'";
//            stmt.executeUpdate(sql);
//
//            // 关闭连接
//            stmt.close();
//            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 异常
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 异常
            e.printStackTrace();
        }
        System.out.println("Goodbye!");
    }
    public boolean insert(T o){
        String simpleName = o.getClass().getSimpleName();
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder placeholdersBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(simpleName).append(" (");

        // 获取实体类的所有字段
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(o);
                if (value != null) {
                    // 拼接字段名和占位符
                    sqlBuilder.append(field.getName()).append(",");
                  if( value instanceof String){
                      placeholdersBuilder.append("\""+value+"\"").append(",");
                  }else {
                      placeholdersBuilder.append(value).append(",");
                  }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 去掉最后一个逗号
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        placeholdersBuilder.deleteCharAt(placeholdersBuilder.length() - 1);

        // 拼接 SQL 语句
        sqlBuilder.append(") VALUES (").append(placeholdersBuilder).append(")");
        // 插入数据
        String sql = sqlBuilder.toString();
        try {
            // 插入数据
            stmt = conn.createStatement();
            boolean execute = stmt.execute(sql);
            stmt.close();
            conn.close();
            return execute;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean insertList(List<T> t) throws SQLException {

        String simpleName = t.get(0).getClass().getSimpleName();
        for(T o:t){
            stmt = conn.createStatement();
            StringBuilder sqlBuilder = new StringBuilder();
            StringBuilder placeholdersBuilder = new StringBuilder();
            sqlBuilder.append("INSERT INTO ").append(simpleName).append(" (");

            // 获取实体类的所有字段
            Field[] fields = o.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(o);
                    if (value != null) {
                        // 拼接字段名和占位符
                        sqlBuilder.append(field.getName()).append(",");
                        if( value instanceof String){
                            placeholdersBuilder.append("\""+value+"\"").append(",");
                        }else {
                            placeholdersBuilder.append(value).append(",");
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // 去掉最后一个逗号
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            placeholdersBuilder.deleteCharAt(placeholdersBuilder.length() - 1);

            // 拼接 SQL 语句
            sqlBuilder.append(") VALUES (").append(placeholdersBuilder).append(")");
            // 插入数据
            String sql = sqlBuilder.toString();
            try {
                // 插入数据
                boolean execute = stmt.execute(sql);
                System.out.println("success");
                stmt.close();
            } catch (SQLException e) {
                if(e.toString().contains("Duplicate")) continue;
                throw new RuntimeException(e);
            }
        }
        conn.close();
     return true;
    }
}
