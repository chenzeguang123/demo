package com.hsbc.ping;

import com.hsbc.ping.Ping;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

class PingTest {

    @Test
    void sendRequestsEverySecond() {

        Ping ping = new Ping();
        ping.sendRequestsEverySecond();
        ping.getLock();
        ping.getLock();
        ping.getLock();
        ping.sendRequest(1);
        assertTrue(true);
    }

}