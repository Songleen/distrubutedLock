package com.atguigu.demo;


import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;

/**
 * @ClassName distributed_lock
 * @Author Songleen
 * @Date 2020/08/30/9:32
 */
public class ConsulUtil {

    private ConsulClient consulClient;
    private String sessionId = null;

    public ConsulUtil(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    /**
     * 创建session 这个session是consul中的session,不是httpServletRequest中的session
     */
    private String createSession(String name, Integer ttl) {
        NewSession newSession = new NewSession();
        // 设置锁有效时长
        newSession.setTtl(ttl + "s");
        // 设置锁的名字
        newSession.setName(name);
        String value = consulClient.sessionCreate(newSession, null).getValue();
        return value;
    }

    /**
     * 获取锁
     */
    public Boolean lock(String name, Integer ttl) {
        // 定义获取标识
        Boolean flag = false;
        // 创建session
        sessionId = createSession(name, ttl);
        // 循环获取锁
        while (true) {
            // 超时操作

            // 执行acquire操作
            PutParams putParams = new PutParams();
            putParams.setAcquireSession(sessionId);
            // name是锁的名字，value是锁的值，putParam是操作类型，这里是acquire
            flag = consulClient.setKVValue(name, "local" + System.currentTimeMillis(), putParams).getValue();
            if (flag) {
                break;
            }
        }
        return flag;
    }

    /**
     * 释放锁
     */
    public Boolean release(String name) {
        // 执行release操作
        PutParams putParams = new PutParams();
        putParams.setReleaseSession(sessionId);
        Boolean value = consulClient.setKVValue(name, "local" + System.currentTimeMillis(), putParams).getValue();
        return value;
    }
}
