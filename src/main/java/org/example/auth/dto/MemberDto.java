package org.example.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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

    @JsonIgnore
    private boolean newMember;

}
