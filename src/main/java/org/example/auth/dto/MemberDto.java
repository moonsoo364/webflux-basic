package org.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto  {

    private String userId;

    private String userName;

    private String password;

    private String userRole;

}
