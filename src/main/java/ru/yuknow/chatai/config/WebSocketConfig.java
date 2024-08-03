package ru.yuknow.chatai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static ru.yuknow.chatai.config.TemplateConfiguration.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ObjectMapper objectMapper;
    private final MessageTemplates messagingTemplates;
    private final OllamaChatModel ollamaChatModel;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SimpleWebsocketHandler(objectMapper, messagingTemplates, ollamaChatModel), "/app/ws")
                .setAllowedOrigins("*");
    }
}
