CREATE TABLE enderecos (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    rua VARCHAR(255),
    numero VARCHAR(10),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    estado VARCHAR(2),
    usuario_id INT UNIQUE, -- Relacionamento um-para-um com usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);