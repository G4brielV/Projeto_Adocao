CREATE TABLE adocoes (
    id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    dono_origem_id BIGINT NOT NULL,  -- Quem está doando/disponibilizando
    adotante_id BIGINT NOT NULL,     -- Quem tem a intenção de adotar
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    mensagem TEXT,                   -- Justificativa ou mensagem inicial do adotante
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_adocao_animal FOREIGN KEY (animal_id) REFERENCES animais(id),
    CONSTRAINT fk_adocao_dono FOREIGN KEY (dono_origem_id) REFERENCES usuarios(id),
    CONSTRAINT fk_adocao_adotante FOREIGN KEY (adotante_id) REFERENCES usuarios(id)
);

-- Índices para otimizar as leituras
CREATE INDEX idx_adocoes_animal ON adocoes(animal_id);
CREATE INDEX idx_adocoes_adotante ON adocoes(adotante_id);
CREATE INDEX idx_adocoes_status ON adocoes(status);