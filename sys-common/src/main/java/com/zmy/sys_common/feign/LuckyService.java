package com.zmy.sys_common.feign;


import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 设置请求地址，并且携带请求头
 */

@Service
@FeignClient(name = "sys-lucky",url = "${map.url}",configuration = FeignConfiguration.class)
public interface LuckyService {
    @RequestMapping(value = "/user/login",method = RequestMethod.POST)
    JSONObject login(@RequestBody Map<String, String> loginMap);
    @PostMapping("/user/profile")
    JSONObject profile();

}

