package com.itheima.controller;

import com.itheima.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/redis")
public class RedisController {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取锁
     * @return
     */
    @GetMapping(value = "/lock/{name}")
    public String lock(@PathVariable(value = "name")String name) throws Exception{
        Boolean result = redisUtil.tryLock(name, 3000L);
        if(result){
            return "获取锁成功";
        }
        return "获取锁失败";
    }

    /**
     * 释放锁
     * @param name
     */
    @GetMapping(value = "/unlock/{name}")
    public String unlock(@PathVariable(value = "name")String name){
        Boolean result = redisUtil.release(name);
        if(result){
            return "释放锁成功";
        }
        return "释放锁失败";
    }
}
