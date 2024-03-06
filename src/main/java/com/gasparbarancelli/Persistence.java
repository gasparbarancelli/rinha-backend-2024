package com.gasparbarancelli;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Persistence {

    private static final Logger LOGGER = Logger.getLogger(Persistence.class.getName());

    private static final Map<String, String> arquivoDeTransacoesPorCliente = new HashMap<>(5);
    private static final Map<String, String> arquivoPorCliente = new HashMap<>(5);
    private final Map<String, Lock> locksByClientId = new HashMap<>(5);

    public Persistence() {
        var basePath = Optional.ofNullable(System.getenv("CSV_BASE_PATH"))
                .orElse("/data");

        Map<String, Integer> limitePorCliente = Map.of(
                "1", 100000,
                "2", 80000,
                "3", 1000000,
                "4", 10000000,
                "5", 500000
        );

        for (String clienteId : limitePorCliente.keySet()) {
            var pathCliente = basePath + File.separator + "cliente-" + clienteId + ".csv";
            var pathTransacao = basePath + File.separator + "transacoes-cliente-" + clienteId + ".csv";
            //"D:\\Projetos\\rinha-persistence\\data\\transacoes-cliente-" + clienteId + ".csv";

            arquivoPorCliente.put(clienteId, pathCliente);
            arquivoDeTransacoesPorCliente.put(clienteId, pathTransacao);
            locksByClientId.put(clienteId, new ReentrantLock());

            var cliente = new Cliente(limitePorCliente.get(clienteId));
            create(pathCliente, new String[]{"limite", "saldo"}, cliente.toCSV());
            create(pathTransacao, new String[]{"valor", "tipo", "descricao", "data"});
        }
    }

    public Cliente inserirTransacao(Transacao transacao) {
        Lock lock = locksByClientId.get(transacao.cliente());
        lock.lock();
        try {
            var fileCliente = arquivoPorCliente.get(transacao.cliente());
            var clienteCSV = readFirst(fileCliente);
            var cliente = Cliente.fromCSV(clienteCSV);

            if ("d".equals(transacao.tipo())
                    && cliente.getSaldoComLimite() < transacao.valor()) {
                throw new RuntimeException();
            }

            var transacaoCSV = transacao.toCSV();
            write(arquivoDeTransacoesPorCliente.get(transacao.cliente()), transacaoCSV);

            cliente.atualizaSaldo(transacao.valor(), transacao.tipo());
            updateFirst(fileCliente, cliente.toCSV());

            return cliente;
        } finally {
            lock.unlock();
        }
    }

    private void create(String filePath, String[]... data) {
        var file = new File(filePath);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
                for (String[] line : data) {
                    writer.writeNext(line);
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Falha ao criar csv");
            }
        }
    }

    private TransacaoSizeCollection read(String filePath) {
        TransacaoSizeCollection data = new TransacaoSizeCollection();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            data.addAll(reader.readAll());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao ler csv");
        }
        return data;
    }

    private String[] readFirst(String filePath) {
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).build()) {
            reader.readNext();
            return reader.readNext();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Falha ao ler csv");
            throw new RuntimeException(e);
        }
    }

    private void write(String filePath, String[] data) {
        var allData = read(filePath);
        allData.addItem(data);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(allData.getList());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Falha ao inserir transacao");
        }
    }

    private void updateFirst(String filePath, String[] data) {
        var allData = read(filePath);
        allData.changeFirst(data);
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(allData.getList());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Falha ao inserir transacao");
        }
    }

    public List<ExtratoTransacao> findTransacoes(String cliente) {
        var path = arquivoDeTransacoesPorCliente.get(cliente);
        var data = read(path);
        return data.stream()
                .skip(1)
                .map(ExtratoTransacao::fromCSV)
                .toList()
                .reversed();
    }

    public Cliente getCliente(String cliente) {
        var path = arquivoPorCliente.get(cliente);
        var data = read(path);
        return Cliente.fromCSV(data.getFirst());
    }

}
