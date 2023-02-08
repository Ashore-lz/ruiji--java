package com.lz.test;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 使用Jedis操作Redis
 */
public class JedisTest {

    @Test
    public void testRedis(){
        //获取链接
        Jedis jedis = new Jedis("localhost", 6379);

        //执行具体操作
        jedis.set("username", "xiaoming");

        String username = jedis.get("username");
        System.out.println(username);

        //jedis.del("username");
        jedis.hset("myhash", "addr", "bj");
        String hValue = jedis.hget("myhash", "addr");
        System.out.println(hValue);

        //输出所有值
        Set<String> keys = jedis.keys("*");

        //关闭链接
        jedis.close();

    }
}
