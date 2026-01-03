package com.websockettest.kafka;

import com.websockettest.websocket.TickerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.kafka.consumer-enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class TickerKafkaConsumer {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.redis.channel:upbit:ticker}")
    private String channel;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        // Kafka → Redis fan-out
        stringRedisTemplate.convertAndSend(channel, message);
    }

    /*private final TickerWebSocketHandler tickerWebSocketHandler;

    @Value("${app.kafka.topic}")
    private String topic;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received from Kafka: {}", message);
        // 그대로 브라우저로 푸시 (필요하면 여기서 가공)
        tickerWebSocketHandler.broadcast(message);
    }*/
}
