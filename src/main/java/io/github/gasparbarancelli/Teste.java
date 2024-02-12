package io.github.gasparbarancelli;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Teste {

    private final Map<Integer, Cliente> mapCliente = new HashMap<>(5);
    private final Map<Integer, List<Transacao>> mapClienteTransacoes = new HashMap<>(5);
    private final Map<Integer, Lock> locks = new HashMap<>(5);

    public Teste() {
        this.mapCliente.put(1, new Cliente(1, 100000));
        this.mapCliente.put(2, new Cliente(2, 80000));
        this.mapCliente.put(3, new Cliente(3, 1000000));
        this.mapCliente.put(4, new Cliente(4, 10000000));
        this.mapCliente.put(5, new Cliente(5, 500000));

        for (int i = 1; i <= 5; i++) {
            mapClienteTransacoes.put(i, new ArrayList<>(10));
            locks.put(i, new ReentrantLock());
        }
    }

    public TransacaoResposta efetuarTransacao(Transacao transacao) throws Exception {
        Lock lock = locks.get(transacao.getCliente());
        lock.lock();
        try {
            var cliente = mapCliente.get(transacao.getCliente());

            if (TipoTransacao.d.equals(transacao.getTipo())
                    && cliente.getSaldoComLimite() < transacao.getValor()) {
                throw new Exception("");
            }

            var clienteTransacoes = mapClienteTransacoes.get(transacao.getCliente());
            if (clienteTransacoes.size() >= 10) {
                clienteTransacoes.removeLast();
            }
            clienteTransacoes.addFirst(transacao);

            cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());

            return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
        } finally {
            lock.unlock();
        }
    }

    public ExtratoResposta extrato(int clienteId) {
        Lock lock = locks.get(clienteId);
        lock.lock();
        try {
            var cliente = mapCliente.get(clienteId);
            var transacoes = mapClienteTransacoes.get(clienteId)
                    .stream()
                    .map(ExtratoResposta.ExtratoTransacaoResposta::gerar)
                    .toList();

            return new ExtratoResposta(
                    new ExtratoResposta.ExtratoSaldoResposta(cliente.getSaldo(), LocalDateTime.now(), cliente.getLimite()),
                    transacoes
            );
        } finally {
            lock.unlock();
        }
    }

}
