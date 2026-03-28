package com.adocao.Projeto_Adocao.Infra.Exception;

// 404 - Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}