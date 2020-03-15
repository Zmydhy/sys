package com.zmy.sys_lucky;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmy.jar_test.log.Log;
import com.zmy.sys_common.utils.JwtUtils;
import com.zmy.sys_lucky.config.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

//1.配置springboot的包扫描
@SpringBootApplication(exclude = MongoAutoConfiguration.class,scanBasePackages = "com.zmy")
//2.配置jpa注解的扫描
@EntityScan(value="com.zmy.sys_moudle.lucky.entity")
@ComponentScan(value = "com.zmy")
public class SysLuckyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SysLuckyApplication.class, args);
    }
    //解决no session
    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }



}
