package org.example.common.model;

import lombok.Data;

import java.time.LocalDateTime;

public class LocaleDateEntity {
    // 생성 날짜 : db 기본값 = UTC_TIMESTAMP
    protected LocalDateTime regDt;
    // 수정 날짜 : db 기본값 null
    protected LocalDateTime updDt;
    //지역 코드 : db 기본값 = ko-KR
    protected String localeCode;
    //시간대 : db 기본값 = Asia/Seoul
    protected String timeZoneCode;
}
