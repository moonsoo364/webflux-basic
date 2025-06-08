package org.example.auth.dto;

import org.example.auth.model.Member;

public record CheckUserDto(
        String userId,
        String memberName,
        Boolean isExistsUser
){

    public CheckUserDto(MemberDto member){
        this(member.getUserId(), member.getMemberName(), true);
    }
    public CheckUserDto(){
        this(null, null, false);
    }
}
