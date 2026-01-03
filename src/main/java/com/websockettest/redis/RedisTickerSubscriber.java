package com.websockettest.redis;

import com.websockettest.websocket.TickerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTickerSubscriber implements MessageListener {

    private final TickerWebSocketHandler tickerWebSocketHandler;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        String payload = message.toString(); // 이미 문자열 payload
        tickerWebSocketHandler.broadcast(payload);
    }
}

