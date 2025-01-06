CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    nome VARCHAR(255) NOT NULL,
    identificador VARCHAR(18) NOT NULL UNIQUE, -- Garantia no banco
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(15),
    saldo NUMERIC(10, 2) DEFAULT 0.00
);
