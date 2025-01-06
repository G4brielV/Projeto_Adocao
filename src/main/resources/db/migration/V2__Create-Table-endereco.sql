CREATE TABLE endereco (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    rua VARCHAR(255),
    numero VARCHAR(10),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(2),
    latitude NUMERIC(10, 8),
    longitude NUMERIC(11, 8),
    usuario_id INT NOT NULL, -- Um usuário pode ter mais de um endereço
    FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);