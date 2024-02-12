package io.github.gasparbarancelli;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Teste {

    private final Map<Integer, Cliente> mapCliente = new ConcurrentHashMap<>(5);
    private final Map<Integer, CircularQueue<Transacao>> mapClienteTransacoes = new ConcurrentHashMap<>(5);

    public Teste() {
        this.mapCliente.put(1, new Cliente(1, 100000));
        this.mapCliente.put(2, new Cliente(2, 80000));
        this.mapCliente.put(3, new Cliente(3, 1000000));
        this.mapCliente.put(4, new Cliente(4, 10000000));
        this.mapCliente.put(5, new Cliente(5, 500000));

        for (int i = 1; i <= 5; i++) {
            mapClienteTransacoes.put(i, new CircularQueue<>(10));
        }
    }

    public synchronized TransacaoResposta efetuarTransacao(Transacao transacao) throws Exception {
        var cliente = mapCliente.get(transacao.getCliente());

        if (TipoTransacao.d.equals(transacao.getTipo())
                && cliente.getSaldoComLimite() < transacao.getValor()) {
            throw new Exception("");
        }

        var clienteTransacoes = mapClienteTransacoes.get(transacao.getCliente());
        clienteTransacoes.add(transacao);

        cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());

        return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
    }

    public ExtratoResposta extrato(Integer clienteId) {
        var cliente = mapCliente.get(clienteId);
        var transacoes = mapClienteTransacoes.get(clienteId)
                .stream()
                .map(ExtratoResposta.ExtratoTransacaoResposta::gerar)
                .toList();

        return new ExtratoResposta(
                new ExtratoResposta.ExtratoSaldoResposta(cliente.getSaldo(), LocalDateTime.now(), cliente.getLimite()),
                transacoes
        );
    }

}
