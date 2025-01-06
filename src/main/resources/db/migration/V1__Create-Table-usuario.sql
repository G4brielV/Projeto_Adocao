CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    nome VARCHAR(255) NOT NULL,
    identificador VARCHAR(18) NOT NULL, -- Armazena CPF ou CNPJ
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(15),
    saldo BIGINT DEFAULT NULL -- Saldo armazenado em centavos (ex.: 10000 = R$ 100,00)
);