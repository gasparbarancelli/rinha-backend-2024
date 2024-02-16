package io.github.gasparbarancelli;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class Database {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void inicializar() {
        try {
            var inserirClientes = Optional.ofNullable(System.getenv("DATABASE_INSERT")).map(Boolean::valueOf).orElse(false);
            if (inserirClientes) {
                var cliente = entityManager.find(Cliente.class, 1);
                if (Objects.isNull(cliente)) {
                    entityManager.createNativeQuery("""
                        INSERT INTO CLIENTE (ID, LIMITE, SALDO)
                        VALUES (1, 100000, 0),
                               (2, 80000, 0),
                               (3, 1000000, 0),
                               (4, 10000000, 0),
                               (5, 500000, 0);
                """).executeUpdate();
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
    }
}