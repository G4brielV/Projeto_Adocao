CREATE TABLE animal (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    nome VARCHAR(255) NOT NULL,
    raca VARCHAR(50) NOT NULL,
    cor VARCHAR(50),
    porte VARCHAR(50),
    nascimento DATE,
    castracao BOOLEAN,
    descricao TEXT,
    usuario_id INT NOT NULL, -- Referência ao usuario que cadastrou o animal
    FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);