package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TransacaoDataSource {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void persisteTransacao(Transacao transacao) {
        entityManager.persist(transacao);
    }

    public Cliente obtemCliente(int id) {
        return entityManager.find(Cliente.class, id);
    }

    public List<Transacao> obtemTransacoesDoCliente(int id) {
        return entityManager.createQuery(
                        """
                                select t
                                from Transacao t
                                where t.cliente = :cliente
                                order by t.data desc
                                limit 10
                                """
                        , Transacao.class)
                .setParameter("cliente", id)
                .getResultList();
    }

}
