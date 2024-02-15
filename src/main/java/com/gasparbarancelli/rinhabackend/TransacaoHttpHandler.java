package com.gasparbarancelli.rinhabackend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

final class TransacaoHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var customExchange = new CustomHttpExchange(exchange);

        var path = customExchange.getPath();

        var split = path.split("/");
        var id = Integer.parseInt(split[2]);

        if (Cliente.naoExiste(id)) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }

        if (customExchange.isPost()) {
            doPost(customExchange, id);
        } else if (customExchange.isGet()) {
            doGet(customExchange, id);
        }
    }

    private void doPost(CustomHttpExchange exchange, int clienteId) {
        try {
            var body = exchange.getBody();
            var transacaoRequisicao = TransacaoMapper.map(body);
            var transacaoResposta = DataSource.insert(transacaoRequisicao.geraTransacao(clienteId));

            var json = TransacaoMapper.map(transacaoResposta);

            exchange.addHeader("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.setBody(json);
        } catch (Exception e) {
            exchange.sendResponseHeaders(422, 0);
        } finally {
            exchange.close();
        }
    }

    private void doGet(CustomHttpExchange exchange, int clienteId) {
        try {
            var extrato = DataSource.extrato(clienteId);
            var json = TransacaoMapper.map(extrato);

            exchange.addHeader("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.setBody(json);
        } finally {
            exchange.close();
        }
    }

}