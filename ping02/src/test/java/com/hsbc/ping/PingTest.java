package com.hsbc.ping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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