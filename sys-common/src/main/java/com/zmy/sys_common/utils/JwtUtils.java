package com.zmy.sys_common.utils;

import com.zmy.jar_test.log.Log;
import com.zmy.sys_common.entity.ResultCode;
import com.zmy.sys_common.exception.CommonExp;
import com.zmy.sys_common.redis.RedisUtils;
import com.zmy.sys_common.redis.SysProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Component
public class JwtUtils {
    //签名私钥
    private static String key = "lucky";
    //签名的失效时间
    private static Long ttl = 15 * 60 * 1000L;
    //redis过期时间 单位秒
    private static int exp = 15 * 60 ;


    private static SysProperties sysProperties;

    @Autowired
    public JwtUtils(SysProperties sysProperties) {
        JwtUtils.sysProperties = sysProperties;
        checkProperty( sysProperties);
    }

    private void checkProperty(SysProperties sysProperties) {
        Optional.ofNullable(sysProperties.getKey()).ifPresent((keys)->key = keys );
        Optional.ofNullable(sysProperties.getTtl()).ifPresent((ttls)->ttl = ttls );
        Optional.ofNullable(sysProperties.getExp()).ifPresent((exps)->exp = exps );
    }


    /**
     * 设置认证token
     * id:登录用户id
     * subject：登录用户名
     */
    public static String createJwt(String id, String name, Map<String, Object> map) {
        //1.设置失效时间
        long now = System.currentTimeMillis();//当前毫秒
        long exps = now + ttl;
        //2.创建jwtBuilder
        JwtBuilder jwtBuilder = Jwts.builder().setId(id).setSubject(name)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, key);
        //3.根据map设置claims
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jwtBuilder.claim(entry.getKey(), entry.getValue());
        }
        jwtBuilder.setExpiration(new Date(exps));
        //4.创建token
        String token = jwtBuilder.compact();
        //5、插入redis
        RedisUtils.set(name, token, exp);
        return token;
    }


    /**
     * 解析token字符串获取clamis
     */
    public static Claims parseJwt(String token) throws CommonExp {
        String username = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        Log.info("用户名："+username);
        //先去检查redis
       String redisToken = (String) RedisUtils.get(username);
       if (redisToken .equals(token)){
           Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
           String newToken = verifyExp(claims);
           Optional.ofNullable(newToken).ifPresent((s) -> claims.put("newtoken", newToken));
           return claims;
       }else {
           throw new CommonExp(ResultCode.REDISNOAUTHORISE);
       }

    }


    /**
     * 验证是否续签
     */
    public static String verifyExp(Claims claims) {

        if (claims != null) {
            //判断是否续签
            Date issuedAt = claims.getIssuedAt();
            Log.info("签发时间:" + DateUtils.format(issuedAt));
            Date expDate = claims.getExpiration();
            Log.info("过期时间:" + DateUtils.format(expDate));
            Long exps = expDate.getTime();
            Long renew = exps - 5 * 60 * 1000L;
            Date renewDate = new Date(renew);
            Log.info("续期时间:" + DateUtils.format(renewDate));
            Date date = new Date();
            if (date.before(expDate) && date.after(renewDate)) {
                //开始续签
                Map<String, Object> map = new HashMap<>();
                map.put("apis", claims.get("apis"));//可访问的api权限字符串
                map.put("department", claims.get("department"));
                String newToken = createJwt(claims.getId(), claims.getSubject(), map);
                //更新redis
                RedisUtils.set(claims.getSubject(), newToken, exp);
                Log.info("续签的token:" + newToken);
                return newToken;
            } else {
                Log.info("不需要续签");
                return null;
            }
        }
        return null;
    }

}
