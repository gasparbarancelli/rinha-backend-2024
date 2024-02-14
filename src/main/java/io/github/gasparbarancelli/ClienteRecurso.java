package io.github.gasparbarancelli;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;

@Path("/clientes")
public class ClienteRecurso {

    @Inject
    EntityManager entityManager;

    @Inject
    ClienteService clienteService;

    public ClienteRecurso() {
        try {
            for (int i = 1; i <= 5; i++) {
                var fileName = String.format("/work/cliente-%d.txt", i);
                var file = new File(fileName);
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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

        String fileName = String.format("/work/cliente-%d.txt", id);
        try (var file = new RandomAccessFile(fileName, "rw");
             var channel = file.getChannel()) {
            var lock = channel.lock();

            var transacao = transacaoRequisicao.geraTransacao(id);
            var transacaoResposta = clienteService.exefutarTransacao(transacao);

            lock.release();

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
