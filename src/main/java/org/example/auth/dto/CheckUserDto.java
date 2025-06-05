package org.example.auth.dto;

import org.example.auth.model.Member;

public record CheckUserDto(
        String userId,
        String userName,
        Boolean isExistsUser
){

    public CheckUserDto(Member member, Boolean isExistsUser){
        this(member.getUserId(), member.getUserRealName(), isExistsUser);
    }
}
