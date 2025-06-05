package org.example.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        String memberName,
        String localeCode
){

}
