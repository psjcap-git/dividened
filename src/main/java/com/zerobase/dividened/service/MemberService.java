package com.zerobase.dividened.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zerobase.dividened.exception.impl.AlreadyExistUserException;
import com.zerobase.dividened.model.Auth;
import com.zerobase.dividened.persist.MemberRepository;
import com.zerobase.dividened.persist.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("couldn't find username -> " + username));
        return null;
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean isExists = memberRepository.existsByUsername(member.getUsername());
        if(isExists) {
            throw new AlreadyExistUserException();
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        MemberEntity memberEntity = member.toEntity();
        return memberRepository.save(memberEntity);
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity memberEntity = memberRepository.findByUsername(member.getUsername())
                                                        .orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다"));

        if(!passwordEncoder.matches(memberEntity.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        return memberEntity;
    }    
}
