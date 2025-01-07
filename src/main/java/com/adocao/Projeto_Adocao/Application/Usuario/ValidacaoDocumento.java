package com.adocao.Projeto_Adocao.Application.Usuario;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VerificadorDocumento.class)
public @interface ValidacaoDocumento {

    String message() default "Documento inválido"; // Mensagem de erro padrão

    Class<?>[] groups() default {}; // Grupos de validação (não usados nesse caso)

    Class<? extends Payload>[] payload() default {}; // Informações adicionais
}
