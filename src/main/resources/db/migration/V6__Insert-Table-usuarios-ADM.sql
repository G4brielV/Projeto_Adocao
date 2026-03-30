-- Criar o Usuário
INSERT INTO usuarios (ativo, nome, cpf, email, senha, telefone)
VALUES (
    TRUE,
    'Admin User',
    '00000000000', -- CPF
    'admin@example.com',
    '$2a$12$IyiwLJI0G35zHjitr/TvreSblHtB2AIxNq/LZvq2MsHoGTSinyUWS',
    '123456789'
);

-- Associar as Roles ao Usuário
INSERT INTO usuario_roles (usuario_id, role_id)
VALUES
    (
        (SELECT id FROM usuarios WHERE email = 'admin@example.com'),
        (SELECT id FROM roles WHERE nome = 'USER')
    ),
    (
        (SELECT id FROM usuarios WHERE email = 'admin@example.com'),
        (SELECT id FROM roles WHERE nome = 'ADMIN')
    );