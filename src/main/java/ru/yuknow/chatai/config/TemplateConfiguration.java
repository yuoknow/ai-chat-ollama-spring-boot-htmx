package ru.yuknow.chatai.config;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfiguration {

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().build();
    }

    @Bean
    public PebbleTemplate messageTemplate(PebbleEngine engine) {
        return engine.getTemplate("static/message.html");
    }
}
