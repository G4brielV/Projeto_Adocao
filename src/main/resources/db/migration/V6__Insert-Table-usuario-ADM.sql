-- Criar o Usuário
INSERT INTO usuario (ativo, nome, cpf, email, senha, telefone, saldo)
VALUES (
    TRUE,
    'Admin User',
    '00000000000', -- CPF
    'admin@example.com',
    '$2a$12$IyiwLJI0G35zHjitr/TvreSblHtB2AIxNq/LZvq2MsHoGTSinyUWS',
    '123456789',
    0.00
);

-- Associar as Roles ao Usuário
INSERT INTO usuario_role (usuario_id, role_id)
VALUES
    (
        (SELECT id FROM usuario WHERE email = 'admin@example.com'),
        (SELECT id FROM role WHERE nome = 'USER')
    ),
    (
        (SELECT id FROM usuario WHERE email = 'admin@example.com'),
        (SELECT id FROM role WHERE nome = 'ADMIN')
    );