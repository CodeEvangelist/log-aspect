package com.y687.entity;

import lombok.Data;

/**
 * 请求日志参数
 * @Author bin.yin
 * @createTime 2019/9/26 15:28
 * @Version
 */
@Data
public class LogEntity {
    /**
     * 请求参数
     */
    private String params;
    /**
     * 请求开始时间
     */
    private Long startTime;
    /**
     * 请求路径
     */
    private String url;
    /**
     * 请求方式
     */
    private String methodType;
    /**
     * 请求类名
     */
    private String className;
}
