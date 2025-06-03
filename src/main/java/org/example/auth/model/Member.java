package org.example.auth.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.auth.dto.MemberDto;
import org.example.common.model.LocaleDateEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table("member") // R2DBC에서 사용하는 Table 매핑
public class Member extends LocaleDateEntity implements UserDetails, Persistable<String> {

    @Id
    @Column("user_id")
    private String userId;

    @Column("user_name")
    private String userName;

    @Column("password")
    private String password;

    @Column("user_role")
    private String userRole;

    @Column("reg_dt")
    private LocalDateTime regDt;

    @Column("upd_dt")
    private LocalDateTime updDt;

    /** Persistable<String> 관련 코드 시작 */
    @Transient
    private boolean newMember;

    @Override
    public String getId() {
        return this.userId;
    }

    @Override
    public boolean isNew() {
        return this.newMember || userId == null;
    }
    /** Persistable<String> 관련 코드 종료*/

    //repository.save() 생성자
    public Member(MemberDto dto) {
        this.userId = dto.getUserId();
        this.password = dto.getPassword();
        this.userName = dto.getUserName();
        this.userRole = dto.getUserRole();
        this.newMember = true;// 명시적으로 새로운 행 추가
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
