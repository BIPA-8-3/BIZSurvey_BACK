package com.bipa.bizsurvey.domain.user.dto;

import com.bipa.bizsurvey.domain.user.enums.Plan;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class LoginUser implements UserDetails, OAuth2User {

    private LoginInfoRequest loginInfoRequest;
    private Map<String, Object> attributes;
    public LoginUser(LoginInfoRequest loginInfoRequest){
        this.loginInfoRequest = loginInfoRequest;
    }

    public LoginUser(LoginInfoRequest loginInfoRequest, Map<String, Object> attributes){
        this.loginInfoRequest = loginInfoRequest;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + loginInfoRequest.getPlanSubscribe());
        return authorities;
    }

    @Override
    public String getPassword() {
        return loginInfoRequest.getPassword();
    }

    @Override
    public String getUsername() {
        return loginInfoRequest.getEmail();
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

    @Override
    public String getName() {
        return null;
    }

    public Long getId() {
        return loginInfoRequest.getId();
    }

    public String getNickname(){
        return loginInfoRequest.getNickname();
    }

    public String getPlan(){
        return String.valueOf(loginInfoRequest.getPlanSubscribe());
    }

    public String getEmail(){
        return loginInfoRequest.getEmail();
    }

}