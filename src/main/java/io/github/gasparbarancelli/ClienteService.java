package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClienteService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public TransacaoResposta exefutarTransacao(Transacao transacao) throws Exception {
        var cliente = entityManager.find(Cliente.class, transacao.getCliente());

        if (TipoTransacao.d.equals(transacao.getTipo())
                && cliente.getSaldoComLimite() < transacao.getValor()) {
            throw new Exception();
        }

        entityManager.persist(transacao);
        cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());
        entityManager.persist(cliente);
        return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
    }

}
