package com.zmy.sys_common.redis;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: zmy
 * Date: 2020/1/8
 * Time: 9:54
 * Description:
 */
@Getter
@Setter
@Configuration
public class SysProperties {

    @Value("${sys.jwt.key}")
    private String key;
    @Value("${sys.jwt.ttl}")
    private long ttl;
    @Value("${sys.redis.exp}")
    private int exp;


}
