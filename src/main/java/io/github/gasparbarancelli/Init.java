package io.github.gasparbarancelli;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class Init {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void incluirClientes() {
        entityManager.createNativeQuery("""
                REPLACE INTO CLIENTE (ID, LIMITE, SALDO)
                VALUES (1, 100000, 0),
                       (2, 80000, 0),
                       (3, 1000000, 0),
                       (4, 10000000, 0),
                       (5, 500000, 0);
        """).executeUpdate();
    }
}