package com.eddy.springAi.biz.chat.service;

import com.eddy.springAi.biz.chat.model.ChatHistory;
import com.eddy.springAi.biz.chat.model.ChatRequest;
import com.eddy.springAi.biz.chat.model.ChatResponse;
import com.eddy.springAi.biz.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final OpenAiChatModel openAiChatModel;

    // 채팅 요청과 AI 응답 처리
    public ChatResponse processChat(ChatRequest chatRequest) {
        // AI 호출
        String aiResponse = callOpenAi(chatRequest.getChatMessge());

        // 채팅 히스토리 저장
        saveChatHistory(chatRequest.getCustomerKey(), "USER", chatRequest.getChatMessge());
        saveChatHistory(chatRequest.getCustomerKey(), "AI", aiResponse);

        // 채팅 요청 저장
        chatRepository.saveChat(chatRequest);

        ChatResponse response = new ChatResponse();

        return response;
    }

    // 채팅 히스토리 저장
    private void saveChatHistory(String customerKey, String sender, String message) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSender(sender);
        chatHistory.setChatMessage(message);
        chatHistory.setChatDttm(LocalDateTime.now());
        chatRepository.saveHist(chatHistory);
    }

    // AI 호출
    private String callOpenAi(String message) {
        String systemPrompt = "당신은 현대자동차 딜러의 상담 역할을 하는 AI입니다. 상담 내용을 전문적으로 처리하세요.\n";

        String fullPrompt = systemPrompt + "User: " + message;
        return openAiChatModel.call(fullPrompt);
    }

    // 특정 고객의 히스토리 조회
    public List<ChatHistory> getChatHistories(String customerKey) {
        return chatRepository.findHistoriesByCustomerKey(customerKey);
    }

}
