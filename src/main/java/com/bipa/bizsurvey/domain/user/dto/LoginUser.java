package com.bipa.bizsurvey.domain.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class LoginUser implements UserDetails {

    private LoginRequest loginRequest;

    public LoginUser(LoginRequest loginRequest){
        this.loginRequest = loginRequest;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + loginRequest.getPlanSubscribe());
        return authorities;
    }

    @Override
    public String getPassword() {
        return loginRequest.getPassword();
    }

    @Override
    public String getUsername() {
        return loginRequest.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
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

    public Long getId() {
        return loginRequest.getId();
    }

    public String getNickname(){
        return loginRequest.getNickname();
    }
}