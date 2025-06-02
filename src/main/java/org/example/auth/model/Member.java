package org.example.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.dto.MemberDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@Table("member") // R2DBC에서 사용하는 Table 매핑
public class Member implements UserDetails {

    @Id
    @Column("user_id")
    private String userId;

    @Column("user_name")
    private String userName;

    @Column("password")
    private String password;

    @Column("user_role")
    private String userRole;

    public Member(MemberDto dto) {
        this.userId = dto.getUserId();
        this.password = dto.getPassword();
        this.userName = dto.getUserName();
        this.userRole = dto.getUserRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요 시 동적으로 변경
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
