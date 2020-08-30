package com.itheima.dao;

import com.itheima.domain.Book;

import java.util.List;

public interface BookDao {

    /**
     * 查询所有的book数据
     * @return
     */
    List<Book> queryBookList(String name) throws Exception;
}
