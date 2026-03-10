CREATE TABLE doacao (
    id SERIAL PRIMARY KEY,
    valor NUMERIC(10, 2) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_doador INT NOT NULL, -- Quem está doando
    id_donatario INT NOT NULL, -- Quem está recebendo a doação
    FOREIGN KEY (id_doador) REFERENCES usuario (id),
    FOREIGN KEY (id_donatario) REFERENCES usuario (id)
);