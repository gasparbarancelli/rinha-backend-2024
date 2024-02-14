package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ClienteService {

    @Inject
    EntityManager entityManager;


    @Transactional
    public TransacaoResposta efetuarTransacao(Transacao transacao, boolean otimista) throws Exception {
        var lockMode = otimista ? LockModeType.WRITE : LockModeType.PESSIMISTIC_WRITE;

        var cliente = entityManager.find(
                Cliente.class,
                transacao.getCliente(),
                lockMode
        );

        if (TipoTransacao.d.equals(transacao.getTipo())
                && cliente.getSaldoComLimite() < transacao.getValor()) {
            throw new Exception();
        }

        entityManager.persist(transacao);
        cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());
        entityManager.persist(cliente);
        return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
    }

    public ExtratoResposta getExtrato(int id) {
        var cliente = entityManager.find(Cliente.class, id);
        var transacoes = entityManager.createQuery(
                        """
                                select t
                                from Transacao t
                                where t.cliente = :cliente
                                order by t.data desc
                                limit 10
                                """
                        , Transacao.class)
                .setParameter("cliente", id)
                .getResultList()
                .stream()
                .map(ExtratoResposta.ExtratoTransacaoResposta::gerar)
                .toList();

        var saldo = new ExtratoResposta.ExtratoSaldoResposta(
                cliente.getSaldo(),
                LocalDateTime.now(),
                cliente.getLimite()
        );

        return new ExtratoResposta(
                saldo,
                transacoes
        );
    }

}
