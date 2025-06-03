package org.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record AuthResponse(
        String token,
        String userName
){

}
