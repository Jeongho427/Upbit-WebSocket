package com.websockettest.upbit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.upbit.collector-enabled", havingValue = "true")
public class UpbitWebSocketClient {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        client = new OkHttpClient();
        connect();
    }

    private void connect() {
        String url = "wss://api.upbit.com/websocket/v1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocketListener listener = new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("Connected to Upbit WebSocket");

                String subscribeMessage = """
                    [
                        {"ticket":"test"},
                        {"type":"ticker","codes":["KRW-BTC","KRW-ETH"],"isOnlyRealtime":"true"}
                    ]
                    """;

                webSocket.send(subscribeMessage);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                log.info("Received text message: {}", text);
                kafkaTemplate.send(topic, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                String json = bytes.string(StandardCharsets.UTF_8);
                log.debug("Received binary message: {}", json);
                kafkaTemplate.send(topic, json);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("Upbit WebSocket error", t);
                reconnectDelay();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.warn("Upbit WebSocket closed: {} - {}", code, reason);
                reconnectDelay();
            }

            private void reconnectDelay() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                connect();
            }
        };

        client.newWebSocket(request, listener);
        client.dispatcher().executorService(); // 애플리케이션 종료 시 정리
    }
}
