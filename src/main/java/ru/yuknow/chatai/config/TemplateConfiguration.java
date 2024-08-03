package ru.yuknow.chatai.config;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TemplateConfiguration {

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().build();
    }

    @Bean
    public MessageTemplates messageTemplates(PebbleEngine engine) {
        var newMessage = engine.getTemplate("static/new-message.html");
        var replaceMessage = engine.getTemplate("static/replace-message.html");
        var emptyMessage = engine.getTemplate("static/empty-message.html");

        return new MessageTemplates(Map.of(
                "newMessage", newMessage,
                "replaceMessage", replaceMessage,
                "emptyMessage", emptyMessage));
    }

    @RequiredArgsConstructor
    public static class MessageTemplates {
        private final Map<String, PebbleTemplate> templates;

        public PebbleTemplate getTemplate(String templateName) {
            return templates.get(templateName);
        }
    }
}
