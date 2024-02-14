package io.github.gasparbarancelli;

import com.coditory.sherlock.DistributedLock;
import com.coditory.sherlock.Sherlock;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@Path("/clientes")
public class ClienteRecurso {

    @Inject
    EntityManager entityManager;

    @Inject
    ClienteService clienteService;

    @Inject
    Sherlock sherlock;


    @POST
    @Path("/{id}/transacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response debitoCredito(@PathParam("id") int id, TransacaoRequisicao transacaoRequisicao) {
        if (Cliente.naoExiste(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!transacaoRequisicao.ehValido()) {
            return Response.status(422).build();
        }

        DistributedLock lock = sherlock.createLock("cliente-" + id);
        try {
            var transacao = transacaoRequisicao.geraTransacao(id);
            var transacaoResposta = clienteService.exefutarTransacao(transacao);
            return Response.ok(transacaoResposta).build();
        } catch (Exception e) {
            return Response.status(422).build();
        } finally {
            lock.release();
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
