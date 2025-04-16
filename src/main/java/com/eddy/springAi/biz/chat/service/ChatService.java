package com.eddy.springAi.biz.chat.service;

import com.eddy.springAi.biz.chat.model.ChatHistory;
import com.eddy.springAi.biz.chat.model.ChatRequest;
import com.eddy.springAi.biz.chat.model.ChatResponse;
import com.eddy.springAi.biz.chat.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final OpenAiChatModel openAiChatModel;

    /**
     * 채팅 요청을 처리하고 AI 응답을 생성합니다.
     *
     * @param chatRequest 사용자 채팅 요청 정보
     * @return 생성된 AI 응답
     */
    public ChatResponse processChat(ChatRequest chatRequest) {



        // 1. 기존 히스토리 로드
        List<ChatHistory> chatHistories = chatRepository.findHistoriesByCustomerKey(chatRequest.getCustomerKey());

        // 2. OpenAI 요청 메시지 빌드
        String payload = buildOpenAiPayload(chatRequest, chatHistories);

        // 3. OpenAI 호출 및 응답 처리
        String aiResponse = openAiChatModel.call(payload);

        // 4. 응답 완성
        ChatResponse response = new ChatResponse();
        response.setChatDttm(LocalDateTime.now());
        response.setChatMessage(aiResponse);
        response.setCustomerKey(chatRequest.getCustomerKey());

        // 5. 대화 히스토리 저장
        saveChatHistory(chatRequest.getId(), chatRequest.getCustomerKey(), "user", chatRequest.getChatMessage(), chatRequest.getChatDttm());
        saveChatHistory(UUID.randomUUID().toString(), chatRequest.getCustomerKey(), "ai", aiResponse, response.getChatDttm());

        return response;
    }

    /**
     * 채팅 히스토리를 저장합니다.
     *
     * @param id          히스토리 ID
     * @param customerKey 고객 고유 키
     * @param sender      메시지 발신자(예: USER, AI)
     * @param message     저장할 메시지 내용
     * @param chatDttm    메시지의 날짜 및 시간
     */
    private void saveChatHistory(String id, String customerKey, String sender, String message, LocalDateTime chatDttm) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(id);
        chatHistory.setCustomerKey(customerKey);
        chatHistory.setSender(sender);
        chatHistory.setChatMessage(message);
        chatHistory.setChatDttm(chatDttm);
        chatRepository.saveHist(chatHistory);
    }


    private String buildOpenAiPayload(ChatRequest chatRequest, List<ChatHistory> chatHistories) {
        // 1. 대화 히스토리를 메시지로 변환
        List<Map<String, String>> messages = chatHistories.stream()
                .map(history -> {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", history.getSender());
                    message.put("content", history.getChatMessage());
                    return message;
                }).collect(Collectors.toList());

        // 2. 현재 메시지 추가
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", chatRequest.getChatMessage());
        messages.add(userMessage);

        // 3. 시스템 메시지 추가
        Map<String, Object> payload = new HashMap<>();
        payload.put("system", "당신은 현대 자동차 딜러 상담 AI입니다. 고객의 차량 구매와 관련된 요청에 대한 안내와 정보를 제공합니다.\n" +
                "요청이 차량 관련 상담인지 아닌지 명확하지 않더라도, 가능한 한 고객의 요청에 성실히 답변하세요.\n" +
                "만약 자동차와 관련이 없는 요청이 확실하다면, 아래와 같이 응답하세요:\n" +
                "\"죄송하지만, 저는 자동차 상담 전용 AI입니다. 차량 관련 상담이 필요하시면 말씀해주세요!\"\n" +
                "답변은 간결하고 대화체로 작성하며, 너무 길지 않게 유지하세요.\n" +
                "고객이 차량 구매에 필요한 정보를 명확히 알 수 있도록 도움을 제공하세요. 예를 들어, 차량 옵션이나 견적, 대리점 정보에 대한 요청에 상세히 답변하세요.");
        payload.put("messages", messages);

        // 4. 추가 정보 포함 (대리점 정보, 차량 정보)
        payload.put("customerKey", chatRequest.getCustomerKey());
        payload.put("dealerInfo", chatRequest.getDealerShop());
        payload.put("carInfo", chatRequest.getCarModel());

        // JSON 문자열로 변환 후 반환
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build OpenAI payload", e);
        }
    }
}
