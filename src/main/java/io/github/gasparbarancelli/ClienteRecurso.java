package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/clientes")
public class ClienteRecurso {

    Teste teste = new Teste();

    @POST
    @Path("/{id}/transacoes")
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response debitoCredito(@PathParam("id") Integer id, TransacaoRequisicao transacaoRequisicao) {
        try {
            var transacao = transacaoRequisicao.geraTransacao(id);
            var transacaoResposta = teste.efetuarTransacao(transacao);
            return Response.ok(transacaoResposta).build();
        } catch (Exception e) {
            return Response.status(422).build();
        }
    }

    @GET
    @Path("/{id}/extrato")
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response extrato(@PathParam("id") Integer id) {
        var extrato = teste.extrato(id);
        return Response.ok(extrato).build();
    }

}
