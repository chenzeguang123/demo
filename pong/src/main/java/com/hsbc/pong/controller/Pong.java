package com.hsbc.pong.controller;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
/**
 * @program: demo-pong
 * @description: 测试
 * @author: czg
 * @create: 2024-06-08 11:44
 **/
@RestController
@RequestMapping(("/pong/m1"))
@Slf4j
public class Pong {

    private static RateLimiter rateLimiter = RateLimiter.create(1.0); // 每秒 1 个许可


    /** 
    * @Description: 对于任何给定的秒，它只能处理1个请求。对于在给定的一秒钟内发出的额外请求，Pong Service应返回429状态代码。
    * @Param: [request]
    * @return: reactor.core.publisher.Mono<org.springframework.http.ResponseEntity<java.lang.String>>
    * @Author: czg
    * @Date: 2024/6/08
    */
    @GetMapping("/query")
    public Mono<ResponseEntity<String>> query(String request) {
        log.info("request:"+request);
        if (rateLimiter.tryAcquire()) {

            return Mono.just(ResponseEntity.ok().body("world"));
        }else {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests, please try again later"));

        }
    }

}
