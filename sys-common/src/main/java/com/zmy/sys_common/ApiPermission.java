package com.zmy.sys_common;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPermission {
    /** 模块 */
    String name() default "";
}
