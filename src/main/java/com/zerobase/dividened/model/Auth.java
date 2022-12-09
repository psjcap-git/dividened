package com.zerobase.dividened.model;

import java.util.List;

import com.zerobase.dividened.persist.entity.MemberEntity;

import lombok.Data;

public class Auth {
    
    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity() {
            return MemberEntity.builder()
                        .username(username)
                        .password(password)
                        .roles(roles)
                        .build();
        }
    }
}
