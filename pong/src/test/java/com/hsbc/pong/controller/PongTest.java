package com.hsbc.pong.controller;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PongTest {

    @Test
    void query() {
        Pong pong =new Pong();

        Mono<ResponseEntity<String>> m1 = pong.query("hello");
        Mono<ResponseEntity<String>> m2 = pong.query("hello");
        m1.subscribe(
                res -> {
                    if(Objects.nonNull(res)){
                        System.out.println(res.getBody());
                        assertEquals(HttpStatus.OK.value(),res.getStatusCodeValue());
                    }
                }
        );

        m2.subscribe(
                res -> {
                    if(Objects.nonNull(res)){
                        System.out.println(res.getBody());
                        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(),res.getStatusCodeValue());
//                        assertEquals(HttpStatus.PRECONDITION_REQUIRED.value(),res.getStatusCodeValue());
                    }
                }
        );
    }
}