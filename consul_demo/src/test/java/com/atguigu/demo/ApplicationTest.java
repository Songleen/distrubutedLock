package com.atguigu.demo;

import com.ecwid.consul.v1.ConsulClient;
import org.junit.jupiter.api.Test;

/**
 * @ClassName distributed_lock
 * @Author Songleen
 * @Date 2020/08/30/11:01
 */
public class ApplicationTest {

    @Test
    public void testLock() throws Exception {
        new Thread(new LockRunner("线程1")).start();
        new Thread(new LockRunner("线程2")).start();
        new Thread(new LockRunner("线程3")).start();
        new Thread(new LockRunner("线程4")).start();
        new Thread(new LockRunner("线程5")).start();
        Thread.sleep(20000L);
    }
}


/**
 * 对象类
 */
class LockRunner implements Runnable{

    // 定义线程的名字
    private String name;

    public LockRunner(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        ConsulUtil lock = new ConsulUtil(new ConsulClient());
        try {
            System.out.println(name+"启动完成，准备开始抢锁！");
            Thread.sleep(1000);
            if (lock.lock("test", 10)){
                System.out.println(name+"获取到了锁！");
                // 持有3秒
                Thread.sleep(3000);
                // 释放锁
                lock.release("test");
                System.out.println(name+"释放了锁！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
