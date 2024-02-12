package io.github.gasparbarancelli;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;

@ApplicationScoped
public class ClienteService {

    @Inject
    TransacaoDataSource transacaoDataSource;
    private final Map<Integer, Lock> locks = new HashMap<>(5);

    public ClienteService() {
        for (int i = 1; i <= 5; i++) {
            locks.put(i, new ReentrantLock());
        }
    }

    public TransacaoResposta efetuarTransacao(Transacao transacao) throws Exception {
        Lock lock = locks.get(transacao.getCliente());
        lock.lock();
        try {
            var cliente = transacaoDataSource.obtemCliente(transacao.getCliente());

            if (TipoTransacao.d.equals(transacao.getTipo())
                    && cliente.getSaldoComLimite() < transacao.getValor()) {
                throw new Exception("");
            }

            cliente.atualizaSaldo(transacao.getValor(), transacao.getTipo());

            try (var virtualThread = newVirtualThreadPerTaskExecutor()) {
                virtualThread.execute(() -> transacaoDataSource.persisteTransacao(transacao));
            }

            return new TransacaoResposta(cliente.getLimite(), cliente.getSaldo());
        } finally {
            lock.unlock();
        }
    }

    public ExtratoResposta extrato(int clienteId) {
        Lock lock = locks.get(clienteId);
        lock.lock();
        try {
            var cliente = transacaoDataSource.obtemCliente(clienteId);
            var transacoes = transacaoDataSource.obtemTransacoesDoCliente(clienteId)
                    .stream()
                    .map(ExtratoResposta.ExtratoTransacaoResposta::gerar)
                    .toList();

            var saldo = new ExtratoResposta.ExtratoSaldoResposta(
                    cliente.getSaldo(),
                    LocalDateTime.now(),
                    cliente.getLimite()
            );

            return new ExtratoResposta(
                    saldo,
                    transacoes
            );
        } finally {
            lock.unlock();
        }
    }

}
