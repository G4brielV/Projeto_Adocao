-- Criar o Usuário
INSERT INTO usuario (ativo, nome, identificador, email, senha, telefone, saldo)
VALUES (
    TRUE,
    'Admin User',
    '00000000000', -- Identificador fictício (CPF)
    'admin@example.com',
    'admin', -- Senha não criptografada (ideal seria usar uma senha hash)
    '123456789',
    0.00
);

-- Associar as Roles ao Usuário
INSERT INTO usuario_role (usuario_id, role_id)
VALUES
    (
        (SELECT id FROM usuario WHERE email = 'admin@example.com'),
        (SELECT id FROM role WHERE nome = 'PESSOA')
    ),
    (
        (SELECT id FROM usuario WHERE email = 'admin@example.com'),
        (SELECT id FROM role WHERE nome = 'CRECHE')
    );