package com.example.demo.login.security.oauth;

import com.example.demo.domain.entity.enums.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private String name;
    private String email;
    private Provider provider;
    private String state;

    public UserInfo(String name, String email, Provider provider, String state) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.state = state;
    }
}