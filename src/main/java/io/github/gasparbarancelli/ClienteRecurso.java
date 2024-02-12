package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
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

        try {
            var transacao = transacaoRequisicao.geraTransacao(id);
            var transacaoResposta = clienteService.efetuarTransacao(transacao);
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

        var extrato = clienteService.extrato(id);
        return Response.ok(extrato).build();
    }

}
