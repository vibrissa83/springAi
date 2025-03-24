package com.eddy.springAi.biz.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatResponse {
    private String customerKey;  // 고객 식별 키
    private String chatMessge;   // 답변 메시지
    private LocalDateTime chatDttm = LocalDateTime.now(); // 답변 생성 시간
}
