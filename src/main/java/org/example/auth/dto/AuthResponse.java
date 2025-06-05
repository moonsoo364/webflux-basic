package org.example.auth.dto;

public record AuthResponse(
        String token,
        String memberName
){

}
