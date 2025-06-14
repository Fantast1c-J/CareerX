package com.careerX.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 用于定义该类是一个控制器，并且每个方法的返回值直接写入HTTP响应体中
@RequestMapping("/api") // 接口的基础路径
public class HealthCheck {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public String checkHealth() {
        return "{\"status\": \"UP\"}";
    }
}
