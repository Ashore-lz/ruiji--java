package com.lz.youtuan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j  //日志
@SpringBootApplication
@ServletComponentScan  //扫描过滤器
@EnableTransactionManagement  //开启事务支持
@EnableCaching  //开启缓存注解功能
public class YoutuanApplication {
    public static void main(String[] args) {
        SpringApplication.run(YoutuanApplication.class, args);
        log.info("项目启动成功");
    }
}
