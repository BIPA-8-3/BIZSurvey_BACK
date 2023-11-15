package com.bipa.bizsurvey.domain.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Getter
public class LoginUser implements UserDetails, OAuth2User {

    private LoginRequest loginRequest;
    private Map<String, Object> attributes;
    public LoginUser(LoginRequest loginRequest){
        this.loginRequest = loginRequest;
    }

    public LoginUser(LoginRequest loginRequest, Map<String, Object> attributes){
        this.loginRequest = loginRequest;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
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

    @Override
    public String getName() {
        return null;
    }
}