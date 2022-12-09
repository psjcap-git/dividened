package com.zerobase.dividened.exception.impl;

import org.springframework.http.HttpStatus;

import com.zerobase.dividened.exception.AbstractException;

public class NoCompanyException extends AbstractException {

    @Override
    public String getMessage() {
        return "존재하지 않는 회사명입니다";
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
    
}
