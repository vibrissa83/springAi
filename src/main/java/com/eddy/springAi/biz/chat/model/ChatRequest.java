package com.eddy.springAi.biz.chat.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ChatRequest {

    private String id = UUID.randomUUID().toString(); // 고유 상담 ID 생성

    @NotBlank(message = "고객 식별 키는 필수입니다.")
    private String customerKey; // 고객 식별 키

    @NotBlank(message = "차종 정보는 필수입니다.")
    private String carModel; // 차종

    @NotBlank(message = "대리점 정보는 필수입니다.")
    private String dealerShop; // 대리점

    @NotBlank(message = "메시지 내용는 필수입니다.")
    private String chatMessage; // 질문내역

    private LocalDateTime chatDttm = LocalDateTime.now(); // 상담 생성 시간
}