CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    ativo BOOLEAN DEFAULT TRUE,
    nome VARCHAR(255) NOT NULL,
    identificador VARCHAR(18) NOT NULL, -- CPF ou CNPJ
    tipo CHAR(1) NOT NULL,
    CONSTRAINT chk_tipo CHECK (tipo IN ('P', 'J')),
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(15),
    saldo BIGINT DEFAULT NULL, -- Saldo armazenado em centavos (ex.: 10000 = R$ 100,00)
    endereco_id INT UNIQUE, -- Relacionamento um-para-um com endereço
    FOREIGN KEY (endereco_id) REFERENCES endereco (id)
);