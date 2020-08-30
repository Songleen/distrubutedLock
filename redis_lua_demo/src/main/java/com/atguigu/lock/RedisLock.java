package com.atguigu.lock;

/**
 * @ClassName distributed_lock
 * @Author Songleen
 * @Date 2020/08/30/22:57
 */
public interface RedisLock {

    /**
     * 获取锁
     * @param realeaseTime 默认释放锁的时间
     * @return 获取锁是否成功
     */
    boolean tryLock(long realeaseTime);

    /**
     * 释放锁
     */
    void unlock();
}
