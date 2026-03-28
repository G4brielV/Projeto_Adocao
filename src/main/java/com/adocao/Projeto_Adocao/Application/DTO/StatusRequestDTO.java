package com.adocao.Projeto_Adocao.Application.DTO;

import jakarta.validation.constraints.NotBlank;

public record StatusRequestDTO(
        @NotBlank
        Boolean status) {
}
