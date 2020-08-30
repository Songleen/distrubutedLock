package com.itheima.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    //定义默认超时时间:单位毫秒，这个是redis内部的超时时间
    private static final Integer LOCK_TIME_OUT = 10000;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 外部调用加锁方法
     */
    public Boolean tryLock(String key, Long timeout) throws Exception {

        //获取当前系统时间设置为开始时间
        Long startTime = System.currentTimeMillis();

        //设置返回默认值-false:加锁失败
        boolean flag = false;

        //死循环获取锁:1.获取锁成功退出 2.获取锁超时退出
        while (true) {
            //判断是否超时
            if ((System.currentTimeMillis() - startTime) >= timeout) {
                break;
            } else {
                //获取锁
                flag = lock(key);
                //判断是否获取成功，成功就直接退出
                if (flag) {
                    // 这里应该设置锁在redis中的过期时间
                    stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
                    break;
                } else {
                    // 获取锁失败就休息0.1秒接着尝试
                    Thread.sleep(100);
                }
            }
        }
        return flag;
    }


    /**
     * 加锁实现
     *
     * @param key
     * @return
     */
    private Boolean lock(String key) {
        return (Boolean) stringRedisTemplate.execute((RedisCallback) redisConnection -> {
            //获取当前系统时间
            Long time = System.currentTimeMillis();

            //设置锁超时时间--这个时间是getset延期的时间，由程序员控制的超时时间
            Long timeout = time + LOCK_TIME_OUT + 1;

            //setnx加锁并获取解锁结果
            Boolean result = redisConnection.setNX(key.getBytes(), String.valueOf(timeout).getBytes());

            //加锁成功返回true
            if (result) {
                return true;
            }

            //加锁失败判断锁是否超时，key是锁的名字，timeout是超时时间
            if (checkLock(key, timeout)) {
                //getset设置值成功后,会返回旧的锁有效时间，这里对锁进行延期
                byte[] newtime = redisConnection.getSet(key.getBytes(), String.valueOf(timeout).getBytes());
                if (time > Long.valueOf(new String(newtime))) {
                    return true;
                }
            }
            //默认加锁失败
            return false;
        });

    }

    /**
     * 释放锁
     */
    public Boolean release(String key) {
        return (Boolean) stringRedisTemplate.execute((RedisCallback) redisConnection -> {
            Long del = redisConnection.del(key.getBytes());
            if (del > 0) {
                return true;
            }
            return false;
        });
    }


    /**
     * 判断锁是否超时
     * key : 锁的名字
     * timeout：超时时间
     */
    private Boolean checkLock(String key, Long timeout) {

        return (Boolean) stringRedisTemplate.execute((RedisCallback) redisConnection -> {
            //获取锁的旧的超时时间
            byte[] bytes = redisConnection.get(key.getBytes());

            try {
                //判断锁的有效时间是否大与当前时间
                if (timeout > Long.valueOf(new String(bytes))) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        });

    }
}
