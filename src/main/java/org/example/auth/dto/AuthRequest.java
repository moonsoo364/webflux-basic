package org.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record AuthRequest(
        @NotBlank String userId,
        @NotBlank String password
){

}