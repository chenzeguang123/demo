package com.hsbc.ping;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @program: demo
 * @description: 发送请求
 * @author: czg
 * @create: 2024-06-11 11:22
 **/

@Component
@Slf4j
public class Ping {


    @PostConstruct //程序启动时自动调用
    public void sendRequestsEverySecond() {

        System.out.println("程序已被调用");
        Flux.interval(Duration.ofSeconds(1)) // 每秒产生一个元素
                .flatMap(number -> {
                    //获取锁
                    if (getLock()) {
                        return sendRequest(1);
                    }else {
                        //速率受限
                        log.error("Speed limited");
                        return Mono.just("Request not sent");
                    }

                })
                .subscribe(
                        response -> log.info("Received response: " + response),
                        error -> log.error(error.getMessage(),error),
                        () -> System.out.println("Completed sending requests")
                );
    }

    /** 
    * @Description: 向pong发送请求
    * @Param: [number]
    * @return: reactor.core.publisher.Mono<java.lang.String>
    * @Author: czg
    * @Date: 2024/6/13
    */
    public Mono<String> sendRequest(int number) {
        
        log.info("send request time :"+LocalDateTime.now());
        WebClient webClient = WebClient.create();
        return webClient.get()
                .uri("http://localhost:8082/pong/m1/query?request={number}",
                        "hello")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> {
                    // 处理异常，比如记录日志、返回默认响应等
                    //log.error("调用pong异常", throwable);
                    return Mono.just("调用"+number+"pong异常->"+throwable.getMessage());
                })  ;
    }


    /** 
    * @Description: 获取文件锁--所有ping试图同时触发Pong Service，则只允许向Pong Service发出2个请求
    * @Param: []
    * @return: boolean
    * @Author: czg
    * @Date: 2024/6/11
    */
    public boolean getLock() {

        File file = new File("D:\\test\\test.txt");
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            FileChannel fileChannel = raf.getChannel();

            // 尝试获取文件锁
            FileLock lock = fileChannel.tryLock();
            if (lock == null) {
                System.out.println("Unable to obtain file lock.");
                return false;
            }

            String content = raf.readLine();
            int now = (int) System.currentTimeMillis() / 1000;
            log.info("获取锁时间"+now+"----"+content);
            if (StringUtil.isNullOrEmpty(content)) {
                raf.write((now + "-1").getBytes());
            } else {
                String[] data = content.split("-");
                String time = data[0];
                String flag = data[1];
                String writeData;
                if (time.equals(Integer.toString(now))) {
                    if (Integer.parseInt(flag) != 1) {
                        return false;
                    } else {
                        writeData = now + "-2";
                    }
                } else {
                    writeData = now + "-1";
                }
                log.info("---"+writeData);
                raf.seek(0); // 将文件指针移回文件开头
                raf.write(writeData.getBytes());
            }

            // 释放锁
            lock.release();
        } catch (Exception e) {
            log.error("获取锁异常->{}",e.getMessage(),e);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }


}
