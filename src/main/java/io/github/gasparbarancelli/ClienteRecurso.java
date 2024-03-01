package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
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
    @RunOnVirtualThread
    public Response debitoCredito(@PathParam("id") int id, TransacaoRequisicao transacaoRequisicao) {
        try {
            var transacao = transacaoRequisicao.geraTransacao(id);
            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("efetuar_transacao")
                .registerStoredProcedureParameter("clienteIdParam", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("tipoParam", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("valorParam", Integer.class, ParameterMode.IN)
                .registerStoredProcedureParameter("descricaoParam", String.class, ParameterMode.IN)
                .registerStoredProcedureParameter("saldoRetorno", Integer.class, ParameterMode.OUT)
                .registerStoredProcedureParameter("limiteRetorno", Integer.class, ParameterMode.OUT)
                .setParameter("clienteIdParam", transacao.getCliente())
                .setParameter("tipoParam", transacao.getTipo().name())
                .setParameter("valorParam", transacao.getValor())
                .setParameter("descricaoParam", transacao.getDescricao());

            storedProcedure.execute();
            var limite = (Integer) storedProcedure.getOutputParameterValue("limiteRetorno");
            var saldo = (Integer) storedProcedure.getOutputParameterValue("saldoRetorno");
            var transacaoResposta = new TransacaoResposta(limite, saldo);
            return Response.ok(transacaoResposta).build();
        } catch (Exception e) {
            return Response.status(422).build();
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
