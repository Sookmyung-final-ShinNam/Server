package com.example.demo.security.oauth.info;

import com.example.demo.entity.enums.LoginState;
import com.example.demo.entity.enums.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {

    private String name;
    private String email;
    private Provider provider;
    private LoginState state;

    public UserInfo(String name, String email, Provider provider, LoginState state) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.state = state;
    }
}