package org.example.common.dto;

import lombok.Builder;

@Builder
public record CommonResponse<T>(
        String resultCode,
        String msg,
        T body) {
}
