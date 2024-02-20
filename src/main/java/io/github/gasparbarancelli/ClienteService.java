package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClienteService {

    @Inject
    EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRED)
    public TransacaoResposta efetuarTransacao(Transacao transacao) {
        var cliente = entityManager.find(Cliente.class, transacao.getCliente(), LockModeType.WRITE);

        if (TipoTransacao.d.equals(transacao.getTipo())
                && cliente.getSaldoComLimite() < transacao.getValor()) {
            throw new ClienteLimiteException();
        }

        entityManager.persist(transacao);
        cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());
        entityManager.persist(cliente);
        return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
    }

}
