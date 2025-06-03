package org.example.common.model;

import lombok.Data;

import java.time.LocalDateTime;

public class LocaleDateEntity {
    // 생성 날짜 : db 기본값 = CURRENT_TIMESTAMP
    protected LocalDateTime regDt;
    // 수정 날짜 : db 기본값 null
    protected LocalDateTime updDt;
    //지역 코드 : db 기본값 = ko-KR
    protected String localeCode;
}
