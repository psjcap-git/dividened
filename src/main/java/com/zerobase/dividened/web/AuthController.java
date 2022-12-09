package com.zerobase.dividened.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zerobase.dividened.model.Auth;
import com.zerobase.dividened.persist.entity.MemberEntity;
import com.zerobase.dividened.security.TokenProvider;
import com.zerobase.dividened.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity memberEntity = memberService.register(request);
        return ResponseEntity.ok(memberEntity);
    }

    @PostMapping("/signin") 
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity memberEntity = memberService.authenticate(request);
        String token = tokenProvider.generateToken(memberEntity.getUsername(), memberEntity.getRoles());
        return ResponseEntity.ok(token);
    }
}
