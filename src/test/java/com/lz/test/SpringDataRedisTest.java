package com.lz.test;

import com.lz.youtuan.YoutuanApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = YoutuanApplication.class)
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 操作String类型数据
     */
    @Test
    public void testString(){
        redisTemplate.opsForValue().set("city", "guangzhou");

        String value =(String) redisTemplate.opsForValue().get("city");
        System.out.println(value);

        redisTemplate.opsForValue().set("key1","value1",10l, TimeUnit.SECONDS);  //指定超时时间

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("city", "changsha"); //不存在则赋值
        System.out.println(aBoolean);
    }

    /**
     * 操作Hash类型数据
     */
    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();

        //存值
        hashOperations.put("002", "name", "xiaoming");
        hashOperations.put("002", "age", "20");
        hashOperations.put("002", "address", "guangzhou");

        //取值
        String age =(String) hashOperations.get("002", "age");
        System.out.println(age);

        //获得hash结构中所有字段
        Set keys = hashOperations.keys("002");
        for(Object key:keys){
            System.out.println(key);
        }

        //获得hash结构中所有字段
        List values = hashOperations.values("002");
        for(Object value:values){
            System.out.println(value);
        }
    }

    /**
     * 操作List类型的数据
     */
    @Test
    public void testList(){
        ListOperations listOperations = redisTemplate.opsForList();

        //存值
        listOperations.leftPush("mylist", "a");
        listOperations.leftPushAll("mylist","b", "c", "d");

        List<String> mylist = listOperations.range("mylist", 0, -1);
        for (String value : mylist) {
            System.out.println(value);
        }

        //获得列表长度
        Long size = listOperations.size("mylist");
        int lsize = size.intValue();
        for (int i = 0; i < lsize; i++) {
            //出队列
            String element =(String) listOperations.rightPop("mylist");
            System.out.println(element);
        }

    }

    /**
     * 操作Set类型数据
     */
    @Test
    public void testSet(){
        SetOperations setOperations = redisTemplate.opsForSet();

        //存值
        setOperations.add("myset", "a","b","c","a");

        //取值
        Set<String> myset = setOperations.members("myset");
        for (String s : myset) {
            System.out.println(s);
        }

        //删除成员
        setOperations.remove("myset", "a", "b");
        myset = setOperations.members("myset");
        for (String s:myset){
            System.out.println(s);
        }
    }

    /**
     * 操作Zset类型数据
     */
    @Test
    public void testZset(){
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        //存值
        zSetOperations.add("myZset","a", 10.0);
        zSetOperations.add("myZset", "b", 11.0);
        zSetOperations.add("myZset", "c", 12.0);
        zSetOperations.add("myZset", "a", 11.5);

        //取值
        Set<String> myZset = zSetOperations.range("myZset", 0, -1);
        for(String s:myZset){
            System.out.println(s);
        }

        //修改分数
        zSetOperations.incrementScore("myZset", "b", 20.0);

        //取值
        myZset = zSetOperations.range("myZset", 0, -1);
        for(String s:myZset){
            System.out.println(s);
        }

        //删除成员
        zSetOperations.remove("myZset", "a", "b");

        //取值
        myZset = zSetOperations.range("myZset", 0, -1);
        for(String s:myZset){
            System.out.println(s);
        }
    }

    /**
     * 通用操作
     */
    @Test
    public void testCommon(){
        //获取Redis所有key
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            System.out.println(key);
        }

        //判断某个key是否存在
        Boolean lz = redisTemplate.hasKey("lz");
        System.out.println(lz);

        //删除指定key
        redisTemplate.delete("myZset");

        //获取指定key对应的value数据类型
        DataType myset = redisTemplate.type("myset");
        System.out.println(myset.name());
    }

}
