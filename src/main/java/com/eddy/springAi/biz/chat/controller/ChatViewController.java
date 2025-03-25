package com.eddy.springAi.biz.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {

    @GetMapping("/chat")
    public String chatPage() {
        return "chat"; // chat.html 찾아 렌더링
    }
}