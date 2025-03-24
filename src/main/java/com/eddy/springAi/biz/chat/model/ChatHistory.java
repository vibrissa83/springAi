package com.eddy.springAi.biz.chat.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatHistory {

    private Long id;

    private String sender; // 메시지 발신자 (USER / AI)

    private String chatMessage; // 메시지 내용

    private LocalDateTime chatDttm;
}