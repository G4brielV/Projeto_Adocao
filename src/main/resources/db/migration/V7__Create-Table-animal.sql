CREATE TABLE animal (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    nome VARCHAR(255) NOT NULL,
    especie VARCHAR(50) NOT NULL,
    cor VARCHAR(50),
    porte VARCHAR(50),
    nascimento DATE,
    castracao BOOLEAN,
    descricao TEXT,
    dono_id INT NOT NULL, -- Referência ao usuário que cadastrou o animal
    FOREIGN KEY (dono_id) REFERENCES usuario (id) ON DELETE CASCADE
);