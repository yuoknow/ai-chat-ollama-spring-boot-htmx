package ru.yuknow.chatai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleWebsocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final PebbleTemplate messageTemplate;
    private final OllamaChatModel ollamaChatModel;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connection established {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("Message received {}", message.getPayload());
        switch (message) {
            case TextMessage textMessage -> {
                var text = objectMapper.readTree(textMessage.getPayload()).get("text");
                session.sendMessage(new TextMessage(createHtmlMessage(text.asText())));

                var answer = ollamaChatModel.call(text.asText());
                session.sendMessage(new TextMessage(createHtmlMessage(answer)));

                log.info("Message sent {}", answer);
            }
            default -> log.warn("Wrong message format {}", message.getPayload());
        }

    }

    private String createHtmlMessage(String text) throws IOException {
        var writer = new StringWriter();
        messageTemplate.evaluate(writer, Map.of("text", text));
        return writer.toString();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Connection closed {}", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
