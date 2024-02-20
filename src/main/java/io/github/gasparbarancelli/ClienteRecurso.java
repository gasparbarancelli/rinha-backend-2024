package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Path("/clientes")
public class ClienteRecurso {

    @Inject
    EntityManager entityManager;

    @Inject
    ClienteService clienteService;

    private final Map<Integer, Lock> locks = new HashMap<>(5);

    public ClienteRecurso() {
        for (int i = 1; i <= 5; i++) {
            locks.put(i, new ReentrantLock());
        }
    }

    @POST
    @Path("/{id}/transacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    @RunOnVirtualThread
    public Response debitoCredito(@PathParam("id") int id, TransacaoRequisicao transacaoRequisicao) {
        if (Cliente.naoExiste(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!transacaoRequisicao.ehValido()) {
            return Response.status(422).build();
        }

        Lock lock = locks.get(id);
        lock.lock();
        try {
            var transacao = transacaoRequisicao.geraTransacao(id);
            return efetuarTransacao(transacao);
        } finally {
            lock.unlock();
        }
    }

    private Response efetuarTransacao(Transacao transacao) {
        try {
            var transacaoResposta = clienteService.efetuarTransacao(transacao);
            return Response.ok(transacaoResposta).build();
        } catch (ClienteLimiteException e) {
            return Response.status(422).build();
        } catch (Exception e) {
            return efetuarTransacao(transacao);
        }
    }

    @GET
    @Path("/{id}/extrato")
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response extrato(@PathParam("id") int id) {
        if (Cliente.naoExiste(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

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

        var extrato = new ExtratoResposta(
                saldo,
                transacoes
        );

        return Response.ok(extrato).build();
    }

}
