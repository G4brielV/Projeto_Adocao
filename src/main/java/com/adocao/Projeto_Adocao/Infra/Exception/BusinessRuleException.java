package com.adocao.Projeto_Adocao.Infra.Exception;

// 400 - Bad Request
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
