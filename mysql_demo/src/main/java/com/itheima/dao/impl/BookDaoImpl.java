package com.itheima.dao.impl;

import com.itheima.dao.BookDao;
import com.itheima.domain.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    /***
     * 查询数据库数据
     * @return
     * @throws Exception
     */
    @Override
    public List<Book> queryBookList(String name) throws Exception {

        // 数据库链接
        Connection connection = null;
        // 预编译statement
        PreparedStatement preparedStatement = null;
        // 结果集
        ResultSet resultSet = null;
        // 图书列表
        List<Book> list = new ArrayList<Book>();

        try {
            // 加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            // 连接数据库
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lucene", "root", "rootlin");
            //关闭自动提交
            connection.setAutoCommit(false);
            // SQL语句,使用当前读进行锁表
            String sql = "SELECT * FROM book where id = 1 for update";
            // 创建preparedStatement
            preparedStatement = connection.prepareStatement(sql);
            // 获取结果集
            resultSet = preparedStatement.executeQuery();
            // 结果集解析
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setName(resultSet.getString("name"));
                list.add(book);
            }
            System.out.println(name + "执行了for update");
            System.out.println("结果为:" + list);
            //锁行后休眠5秒
            Thread.sleep(5000);

            //休眠结束释放
            connection.commit();
            System.out.println(name + "结束");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
