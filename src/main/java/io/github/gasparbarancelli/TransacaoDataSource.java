package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TransacaoDataSource {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void persisteTransacao(Transacao transacao) {
        entityManager.persist(transacao);
    }

}
