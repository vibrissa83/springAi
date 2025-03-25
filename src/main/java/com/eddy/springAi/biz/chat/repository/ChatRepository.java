
package com.eddy.springAi.biz.chat.repository;

import com.eddy.springAi.biz.chat.model.ChatHistory;
import com.eddy.springAi.biz.chat.model.ChatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final DynamoDbClient dynamoDbClient;

    private static final String CHAT_REQUEST_TABLE = "CUST_CHAT";
    private static final String CHAT_HISTORY_TABLE = "CUST_CHAT_HISTORY";

    /**
     * 고객 상담 데이터를 DynamoDB에 저장
     * @param chatRequest - 고객 상담 요청 데이터(Model 객체)
     */
    public void saveChat(ChatRequest chatRequest) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("ID", AttributeValue.builder().s(chatRequest.getId()).build());
        item.put("CUSTOMER_KEY", AttributeValue.builder().s(chatRequest.getCustomerKey()).build());
        item.put("CAR_MODEL", AttributeValue.builder().s(chatRequest.getCarModel()).build());
        item.put("DEALER_SHOP", AttributeValue.builder().s(chatRequest.getDealerShop()).build());
        item.put("CHAT_MESSGE", AttributeValue.builder().s(chatRequest.getChatMessge()).build());
        item.put("CREATED_DTTM", AttributeValue.builder().s(chatRequest.getChatDttm().toString()).build());

        dynamoDbClient.putItem(request -> request.tableName(CHAT_REQUEST_TABLE).item(item));
    }

    /**
     * 채팅 히스토리 데이터를 DynamoDB에 저장
     * @param chatHistory - 채팅 히스토리 데이터(Model 객체)
     */
    public void saveHist(ChatHistory chatHistory) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("ID", AttributeValue.builder().s(chatHistory.getId()).build());
        item.put("CUSTOMER_KEY", AttributeValue.builder().s(chatHistory.getCustomerKey()).build());
        item.put("SENDER", AttributeValue.builder().s(chatHistory.getSender()).build());
        item.put("CHAT_MESSAGE", AttributeValue.builder().s(chatHistory.getChatMessage()).build());
        item.put("MESSAGE_DTTM", AttributeValue.builder().s(chatHistory.getChatDttm().toString()).build());

        dynamoDbClient.putItem(request -> request.tableName(CHAT_HISTORY_TABLE).item(item));
    }

    /**
     * 특정 고객의 전체 채팅 히스토리 데이터를 조회
     * @param customerKey - 고객 식별 키
     * @return List<ChatHistory> - 조회된 채팅 히스토리 데이터 리스트
     */
    public List<ChatHistory> findHistoriesByCustomerKey(String customerKey) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":customerKey", AttributeValue.builder().s(customerKey).build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(CHAT_HISTORY_TABLE)
                .keyConditionExpression("CUSTOMER_KEY = :customerKey")
                .expressionAttributeValues(expressionValues)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        return response.items().stream().map(this::mapToChatHistory).collect(Collectors.toList());
    }

    /**
     * DynamoDB에서 반환된 데이터를 ChatHistory 객체로 변환
     * @param item - DynamoDB에서 반환된 데이터(Map 형식)
     * @return ChatHistory - 변환된 ChatHistory 객체
     */
    private ChatHistory mapToChatHistory(Map<String, AttributeValue> item) {
        ChatHistory history = new ChatHistory();
        history.setId(item.get("ID").s());
        history.setSender(item.get("SENDER").s());
        history.setCustomerKey(item.get("CUSTOMER_KEY").s());
        history.setChatMessage(item.get("CHAT_MESSAGE").s());
        history.setChatDttm(LocalDateTime.parse(item.get("MESSAGE_DTTM").s()));
        return history;
    }
}