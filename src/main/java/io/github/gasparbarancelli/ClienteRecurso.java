package io.github.gasparbarancelli;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@Path("/clientes")
public class ClienteRecurso {

    @Inject
    EntityManager entityManager;

    @POST
    @Path("/{id}/transacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional(Transactional.TxType.REQUIRED)
    public Response debitoCredito(@PathParam("id") int id, TransacaoRequisicao transacaoRequisicao) {
        if (!Cliente.existe(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!transacaoRequisicao.ehValido()) {
            return Response.status(422).build();
        }

        var cliente = entityManager.find(Cliente.class, id, LockModeType.READ);

        if (TipoTransacao.d.equals(transacaoRequisicao.tipo())
                && cliente.getSaldoComLimite() < transacaoRequisicao.valor()) {
            return Response.status(422).build();
        }

        var transacao = transacaoRequisicao.geraTransacao(id);
        entityManager.persist(transacao);
        cliente.atualizaSaldo(transacaoRequisicao.valor(), transacaoRequisicao.tipo());
        entityManager.persist(cliente);
        var transacaoResposta = new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
        return Response.ok(transacaoResposta).build();
    }

    @GET
    @Path("/{id}/extrato")
    @Produces(MediaType.APPLICATION_JSON)
    public Response extrato(@PathParam("id") int id) {
        if (!Cliente.existe(id)) {
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
