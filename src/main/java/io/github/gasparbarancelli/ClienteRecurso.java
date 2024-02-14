package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.transaction.RollbackException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/clientes")
public class ClienteRecurso {

    @Inject
    ClienteService clienteService;

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

        return efetuarTransacao(id, transacaoRequisicao, true);
    }

    Response efetuarTransacao(int clienteId, TransacaoRequisicao transacaoRequisicao, boolean otimista) {
        try {
            var transacaoResposta = clienteService.efetuarTransacao(transacaoRequisicao.geraTransacao(clienteId), otimista);
            return Response.ok(transacaoResposta).build();
        } catch (RollbackException le) {
            return efetuarTransacao(clienteId, transacaoRequisicao, false);
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

        var extrato = clienteService.getExtrato(id);
        return Response.ok(extrato).build();
    }

}
