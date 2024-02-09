package io.github.gasparbarancelli;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.util.Objects;

@ApplicationScoped
public class Database {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void inicializar() {
        try {
            var cliente = entityManager.find(Cliente.class, 1, LockModeType.PESSIMISTIC_READ);
            if (Objects.isNull(cliente)) {
                entityManager.createNativeQuery("""
                        REPLACE INTO CLIENTE (ID, LIMITE, SALDO)
                        VALUES (1, 100000, 0),
                               (2, 80000, 0),
                               (3, 1000000, 0),
                               (4, 10000000, 0),
                               (5, 500000, 0);
            """).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}