package ru.yuknow.chatai.web;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiAnswerController {
    private final OllamaChatModel ollamaChatModel;

    @GetMapping("/api/ai/answer")
    public AiResponse answer(@RequestParam String promt) {
        var chatResponse = ollamaChatModel.call(promt);

        return new AiResponse(chatResponse);
    }

    public record AiResponse(String text) {}
}
