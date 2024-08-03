package ru.yuknow.chatai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static ru.yuknow.chatai.config.TemplateConfiguration.MessageTemplates;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleWebsocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageTemplates messageTemplates;
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
                var messageId = System.currentTimeMillis();
                session.sendMessage(new TextMessage(newMessage(messageId, text.asText())));

                var answerId = messageId + 1;
                session.sendMessage(new TextMessage(emptyMessage(answerId)));
                var sb = new StringBuilder("ai: ");
                ollamaChatModel.stream(text.asText()).handle((answer, sink) -> {
                    try {
                        sb.append(answer);
                        log.info("Message from ai {}", sb);
                        session.sendMessage(new TextMessage(replaceMessage(answerId, sb.toString())));
                    } catch (IOException e) {
                        log.error("Error on sending message to ui {}", answer, e);
                    }
                }).doOnError(e -> log.error("Error", e)).subscribe();
            }
            default -> log.warn("Wrong message format {}", message.getPayload());
        }

    }

    private String emptyMessage(long id) throws IOException {
        var writer = new StringWriter();
        messageTemplates.getTemplate("emptyMessage")
                .evaluate(writer, Map.of("id", "m" + id));
        return writer.toString();
    }

    private String replaceMessage(long id, String text) throws IOException {
        var writer = new StringWriter();
        messageTemplates.getTemplate("replaceMessage")
                .evaluate(writer, Map.of("text", text, "id", "m" + id));
        return writer.toString();
    }

    private String newMessage(long id, String text) throws IOException {
        var writer = new StringWriter();
        messageTemplates.getTemplate("newMessage").evaluate(writer, Map.of("text", text, "id", "m" + id));
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
