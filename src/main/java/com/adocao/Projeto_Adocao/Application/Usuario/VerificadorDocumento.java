package com.adocao.Projeto_Adocao.Application.Usuario;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.InputMismatchException;
import java.util.regex.Pattern;

public class VerificadorDocumento implements ConstraintValidator <ValidacaoDocumento, String>{

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext constraintValidatorContext) {
        if (valor == null) {
            return false; // Caso o valor seja nulo, você pode ou não validá-lo
        }
        return isValidCPF(valor); // Validação se é CPF é valido
    }

    private boolean isValidCPF(String CPF) {
        return true;
    }
}





