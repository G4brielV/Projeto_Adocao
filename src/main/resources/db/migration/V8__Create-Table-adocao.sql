CREATE TABLE adocao (
    id SERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    data TIMESTAMP NOT NULL,
    CONSTRAINT fk_adocao_animal FOREIGN KEY (animal_id) REFERENCES animal(id),
    CONSTRAINT fk_adocao_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);