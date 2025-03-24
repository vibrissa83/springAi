package com.eddy.springAi.biz.chat.controller;

import com.eddy.springAi.biz.chat.model.ChatRequest;
import com.eddy.springAi.biz.chat.service.ChatService;
import com.eddy.springAi.biz.chat.model.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api")
@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;


    @PostMapping("/chat")
    public ChatResponse handleChat(@Valid @RequestBody ChatRequest request) {
        // Service 계층으로 요청 전달
        return chatService.processChat(request);
    }

}