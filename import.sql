CREATE UNLOGGED TABLE public.CLIENTE (
                                ID SERIAL PRIMARY KEY,
                                LIMITE INT,
                                SALDO INT DEFAULT 0
) WITH (autovacuum_enabled = false);

CREATE UNLOGGED TABLE public.TRANSACAO (
                                  ID SERIAL PRIMARY KEY,
                                  CLIENTE_ID INT NOT NULL,
                                  VALOR INT NOT NULL,
                                  TIPO CHAR(1) NOT NULL,
                                  DESCRICAO VARCHAR(10) NOT NULL,
                                  DATA TIMESTAMP NOT NULL
) WITH (autovacuum_enabled = false);

CREATE INDEX IDX_TRANSACAO_CLIENTE_DATA ON TRANSACAO (CLIENTE_ID ASC, DATA DESC);

INSERT INTO public.CLIENTE (ID, LIMITE)
VALUES (1, 100000),
       (2, 80000),
       (3, 1000000),
       (4, 10000000),
       (5, 500000);

CREATE FUNCTION update_saldo()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.tipo = 'd' THEN
        UPDATE CLIENTE SET saldo = saldo - NEW.valor
        WHERE id = NEW.cliente_id;
    ELSE
        UPDATE CLIENTE SET saldo = saldo + NEW.valor
        WHERE id = NEW.cliente_id;
    end if;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_saldo_trigger
    AFTER INSERT ON TRANSACAO
    FOR EACH ROW EXECUTE PROCEDURE update_saldo();