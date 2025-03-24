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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final OpenAiChatModel openAiChatModel;

    // 채팅 요청과 AI 응답 처리
    public ChatResponse processChat(ChatRequest chatRequest) {

        //응답내용 반환
        ChatResponse response = new ChatResponse();

        // 채팅 요청 저장
        chatRepository.saveChat(chatRequest);

        // AI 호출
        String aiResponse = callOpenAi(chatRequest.getChatMessge());

        // 채팅 히스토리 저장
        saveChatHistory(chatRequest.getId(), chatRequest.getCustomerKey(), "USER", chatRequest.getChatMessge(), chatRequest.getChatDttm());
        saveChatHistory(UUID.randomUUID().toString(), chatRequest.getCustomerKey(), "AI", aiResponse, response.getChatDttm());

        response.setChatDttm(LocalDateTime.now());
        response.setChatMessge(aiResponse);
        response.setCustomerKey(chatRequest.getCustomerKey());

        return response;
    }

    // 채팅 히스토리 저장
    private void saveChatHistory(String id, String customerKey, String sender, String message, LocalDateTime chatDttm) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(id);
        chatHistory.setCustomerKey(customerKey);
        chatHistory.setSender(sender);
        chatHistory.setChatMessage(message);
        chatHistory.setChatDttm(chatDttm);
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
