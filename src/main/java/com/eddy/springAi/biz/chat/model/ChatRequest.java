package com.eddy.springAi.biz.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatRequest {

    private String id = UUID.randomUUID().toString(); // 고유 상담 ID 생성

    private String customerKey; // 고객 식별 키

    private String carModel; // 차종

    private String dealerShop; // 대리점

    private String chatMessge; // 질문내역

    private LocalDateTime chatDttm = LocalDateTime.now(); // 상담 생성 시간
}