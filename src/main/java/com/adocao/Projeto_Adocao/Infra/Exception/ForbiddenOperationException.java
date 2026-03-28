package com.adocao.Projeto_Adocao.Infra.Exception;

// 403 - Forbidden
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
