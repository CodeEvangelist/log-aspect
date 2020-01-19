package com.y687.aspect;

import com.alibaba.fastjson.JSON;
import com.y687.entity.LogEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.y687.constants.LogAspectExcution.MAPPING_EXCUTION;

/**
 * @author bin.yin
 * @version
 * @date 2019/9/26 15:58
 * @description feign调用切面
 */
@Aspect
@Slf4j
@Component
public class LogAspect {

     /**
      * 每个线程的调用数据
      */
    private ThreadLocal<Stack<LogEntity>> logThread = new ThreadLocal<>();

    /**
     *切入点
     * @Author bin.yin
     * @createTime 2019/9/26 15:22
     * @param
     * @return void
     * @throws
     * @version v-zhibo-develop
     */
    @Pointcut(MAPPING_EXCUTION)
    public void logPoint() {
    }

    /**
     * 调用前
     * @Author bin.yin
     * @createTime 2019/9/26 15:24
     * @param joinPoint
     * @return void
     * @throws
     * @version master {频道需求}
     */
    @Before("logPoint()")
    public void doBefore(JoinPoint joinPoint) {
        Stack<LogEntity> logStack = Optional.ofNullable(logThread.get()).orElse(new Stack<>());
        try {
            LogEntity logEntity = new LogEntity();
            Signature signature = joinPoint.getSignature();
            Method method = ((MethodSignature) signature).getMethod();
            Class<?> declaringClass = method.getDeclaringClass();
            String name = declaringClass.getName();
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            RequestMethod[] methods = null;
            String[] value = null;
            if(getMapping != null){
                methods = new RequestMethod[]{RequestMethod.GET};
                value = getMapping.value();
            }
            if(postMapping != null){
                methods = new RequestMethod[]{RequestMethod.POST};
                value = postMapping.value();
            }
            if(requestMapping != null){
                methods = requestMapping.method();
                value = requestMapping.value();
            }
            Object[] paramsArray = joinPoint.getArgs();
            List<Object> paramList = Arrays
                    .stream(paramsArray)
                    .filter(p -> !(p instanceof ServletRequest)
                            && !(p instanceof ServletResponse)
                            && !(p instanceof MultipartFile))
                    .collect(Collectors.toList());
            logEntity.setParams(JSON.toJSONString(paramList));
            logEntity.setStartTime(System.currentTimeMillis());
            logEntity.setUrl(JSON.toJSONString(value));
            logEntity.setMethodType(JSON.toJSONString(methods));
            logEntity.setClassName(name);
            logStack.push(logEntity);
            logThread.set(logStack);
        } catch (Exception e) {
            log.error("feign切入前操作异常:{}", e.getMessage(),e);
        }
    }

    /**
     * 调用后
     * @Author bin.yin
     * @createTime 2019/9/26 15:42
     * @param ret
     * @return void
     * @throws
     * @version v-zhibo-develop
     */
    @AfterReturning(returning = "ret", pointcut = "logPoint()")
    public void doAfterReturning(Object ret) {
        Stack<LogEntity> logstack = logThread.get();
        try {
            StringBuilder sb = new StringBuilder(1000);
            //得到保存在本地线程中的请求方法名，方法的返回值类型
            String result = JSON.toJSONString(ret);
            if (result.length() > 100) {
                result = result.substring(0, 100);
            }
            LogEntity logEntity = logstack.pop();
            //打印日志
            sb.append("\n");
            sb.append("---------------------------------------------------------------------------------------------").append("\n");
            sb.append("请求类名:  ").append(logEntity.getClassName()).append("\n");
            sb.append("请求方法:  ").append(logEntity.getUrl()).append("\n");
            sb.append("请求方式:  ").append(logEntity.getMethodType()).append("\n");
            sb.append("请求参数:  ").append(logEntity.getParams()).append("\n");
            sb.append("处理时间:  ").append(System.currentTimeMillis() - logEntity.getStartTime()).append(" 毫秒").append("\n");
            sb.append("返回值  :  ").append(result).append("\n");
            sb.append("---------------------------------------------------------------------------------------------");
            log.info(sb.toString());
        } catch (Exception e) {
            log.error("feign切入后操作异常:{}", e.getMessage(),e);
        }finally {
            if(logstack != null && logstack.isEmpty()){
                logThread.remove();
            }
        }
    }
}
