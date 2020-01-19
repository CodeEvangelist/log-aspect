package com.y687.annotation;

import com.y687.config.LogAspectConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description
 *
 * @Author bin.yin
 * @createTime 2020/1/19 14:53
 * @Version
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogAspectConfig.class)
@Documented
@Inherited
public @interface EnableAspect {

}
