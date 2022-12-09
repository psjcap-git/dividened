package com.zerobase.dividened.exception.impl;

import org.springframework.http.HttpStatus;

import com.zerobase.dividened.exception.AbstractException;

public class AlreadyExistCompanyException extends AbstractException {
    
    @Override
    public String getMessage() {
        return "이미 존재하는 회사입니다";
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
