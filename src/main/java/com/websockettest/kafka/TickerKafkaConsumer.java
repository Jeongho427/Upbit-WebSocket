package com.websockettest.kafka;

import com.websockettest.websocket.TickerWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.kafka.consumer-enabled",
        havingValue = "true",
        matchIfMissing = true   // 기본값 true (그냥 안 적으면 켜짐)
)
public class TickerKafkaConsumer {

    private final TickerWebSocketHandler tickerWebSocketHandler;

    @Value("${app.kafka.topic}")
    private String topic;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received from Kafka: {}", message);
        // 그대로 브라우저로 푸시 (필요하면 여기서 가공)
        tickerWebSocketHandler.broadcast(message);
    }
}
