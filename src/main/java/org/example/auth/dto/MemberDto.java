package org.example.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto  {

    @NotBlank
    private String userId;

    @NotBlank
    private String memberName;

    @NotBlank
    private String password;

    private String userRole;

    private String localeCode;

}
