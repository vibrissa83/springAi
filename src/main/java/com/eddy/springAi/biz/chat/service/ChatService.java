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

    /**
     * 채팅 요청을 처리하고 AI 응답을 생성합니다.
     *
     * @param chatRequest 사용자 채팅 요청 정보
     * @return 생성된 AI 응답
     */
    public ChatResponse processChat(ChatRequest chatRequest) {

        ChatResponse response = new ChatResponse();

        // 채팅 요청 저장 (최신 요청을 기록)
        chatRepository.saveChat(chatRequest);

        // 1. 기존 히스토리 조회 - 특정 CustomerKey로 이전 대화 불러오기
        List<ChatHistory> chatHistories = chatRepository.findHistoriesByCustomerKey(chatRequest.getCustomerKey());

        // 2. Open AI 호출
        String aiResponse;
        if (chatHistories.isEmpty()) {
            // 히스토리가 없을 경우: 초기 인사말과 요청 정보 전달
            aiResponse = callOpenAiFirst(chatRequest);
        } else {
            // 히스토리가 있을 경우: 대화 맥락을 AI에 전달하여 응답 생성
            aiResponse = callOpenAi(chatRequest, chatHistories);
        }

        // 아스트릭트 제거
        aiResponse = aiResponse.replace("*", "");

        // 3. 대화 히스토리 저장
        saveChatHistory(chatRequest.getId(), chatRequest.getCustomerKey(), "USER", chatRequest.getChatMessge(), chatRequest.getChatDttm());
        saveChatHistory(UUID.randomUUID().toString(), chatRequest.getCustomerKey(), "AI", aiResponse, response.getChatDttm());

        // 응답 완성
        response.setChatDttm(LocalDateTime.now());
        response.setChatMessge(aiResponse);
        response.setCustomerKey(chatRequest.getCustomerKey());

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

    /**
     * 이전 채팅 히스토리를 참조하여 Open AI를 호출합니다.
     *
     * @param chatRequest   사용자 채팅 요청 정보
     * @param chatHistories 해당 사용자와 관련된 이전 히스토리 목록
     * @return Open AI의 응답 메시지
     */
    private String callOpenAi(ChatRequest chatRequest, List<ChatHistory> chatHistories) {
        // 1. System Prompt 설정
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append("당신은 현대자동차 딜러의 상담 역할을 하는 AI 입니다.\n" +
                "답변할때 '*'를 쓰지 않습니다. 'AI:' 같은 말머리도 붙이지 않습니다.\n" +
                "자연스럽게 대화하듯이 답변을 해주세요.\n" +
                "다음은 대화 기록입니다. 대화를 이어가 주세요.\n\n");

        // 2. 이전 대화 내역 추가
        for (ChatHistory history : chatHistories) {
            fullPrompt.append(history.getSender()).append(": ").append(history.getChatMessage()).append("\n");
        }

        // 3. 요청 정보 추가
        fullPrompt.append("고객 요청 정보:\n");
        fullPrompt.append("- 대리점: ").append(chatRequest.getDealerShop()).append("\n");
        fullPrompt.append("- 차종: ").append(chatRequest.getCarModel()).append("\n\n");

        // 4. 사용자의 최신 메시지 추가
        fullPrompt.append("- 사용자의 채팅 : ").append(chatRequest.getChatMessge()).append("\n");

        // 5. AI 호출
        return openAiChatModel.call(fullPrompt.toString());
    }

    /**
     * 사용자의 첫 요청에 대해 Open AI를 호출합니다.
     *
     * @param chatRequest 사용자 채팅 요청 정보
     * @return Open AI의 응답 메시지
     */
    private String callOpenAiFirst(ChatRequest chatRequest) {
        // 1. System Prompt 작성
        StringBuilder fullPrompt = new StringBuilder();
        fullPrompt.append("당신은 현대자동차 딜러의 상담 역할을 하는 AI입니다.\n" +
                "답변할때 '*'를 쓰지 않습니다. 'AI:' 같은 말머리도 붙이지 않습니다.\n" +
                "자연스럽게 대화하듯이 답변을 해주세요.\n"+
                "아래 정보를 참고해 응답 해주세요.\n\n");

        // 2. 요청 정보 추가
        fullPrompt.append("고객 요청 정보:\n");
        fullPrompt.append("- 대리점: ").append(chatRequest.getDealerShop()).append("\n");
        fullPrompt.append("- 차종: ").append(chatRequest.getCarModel()).append("\n\n");

        // 3. 사용자 초기 메시지 추가
        fullPrompt.append("- 사용자의 채팅 : ").append(chatRequest.getChatMessge()).append("\n");

        // 4. AI 호출
        return openAiChatModel.call(fullPrompt.toString());
    }


    /**
     * 특정 고객에 대한 채팅 히스토리를 조회합니다.
     *
     * @param customerKey 조회할 고객 고유 키
     * @return 해당 고객의 채팅 히스토리 리스트
     */
    public List<ChatHistory> getChatHistories(String customerKey) {
        return chatRepository.findHistoriesByCustomerKey(customerKey);
    }

}
