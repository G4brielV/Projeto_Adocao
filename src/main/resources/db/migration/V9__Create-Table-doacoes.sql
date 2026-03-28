CREATE TABLE doacoes (
    id BIGSERIAL PRIMARY KEY,
    valor NUMERIC(10, 2) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_doador BIGINT NOT NULL, -- Quem está doando
    id_donatario BIGINT NOT NULL, -- Quem está recebendo a doação
    FOREIGN KEY (id_doador) REFERENCES usuarios (id),
    FOREIGN KEY (id_donatario) REFERENCES usuarios (id)
);