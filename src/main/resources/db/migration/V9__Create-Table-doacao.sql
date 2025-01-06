CREATE TABLE doacao (
    id SERIAL PRIMARY KEY,
    valor NUMERIC(10, 2) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_doador INT NOT NULL, -- Quem está doando
    id_creche INT NOT NULL, -- Para quem está doando
    FOREIGN KEY (id_doador) REFERENCES usuario (id),
    FOREIGN KEY (id_creche) REFERENCES usuario (id)
);