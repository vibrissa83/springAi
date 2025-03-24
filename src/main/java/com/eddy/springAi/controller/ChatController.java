package com.eddy.springAi.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class ChatController {
    private final OpenAiChatModel openAiChatModel;

    public ChatController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody String message) {
        Map<String, String> responses = new HashMap<>();

        String systemPrompt = "당신은 현대자동차 딜러사의 고객 상담사 역할을 하는 AI입니다.\n" +
                "고객에게 현대자동차 관련 차량 정보, 딜러사 위치, 서비스 센터 안내, 차량 구매 및 서비스 상담을 제공하세요.\n" +
                "정중하고 친절하게 응답하며, 고객의 질문에 전문적이고 정확하게 대답하세요.\n";

        String fullPrompt = systemPrompt + "\nUser: " + message;

        String openAiResponse = openAiChatModel.call(fullPrompt);
        responses.put("openai(chatGPT) 응답", openAiResponse);
        return responses;
    }
}