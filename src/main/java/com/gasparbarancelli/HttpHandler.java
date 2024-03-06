package com.gasparbarancelli;

import com.sun.net.httpserver.HttpExchange;

import java.time.LocalDateTime;

public class HttpHandler implements com.sun.net.httpserver.HttpHandler {

    private final Persistence persistence = new Persistence();
    private final Mapper mapper = new Mapper();

    @Override
    public void handle(HttpExchange exchange) {
        var customExchange = new CustomHttpExchange(exchange);
        if (customExchange.isPost()) {
            doPost(customExchange);
            return;
        }

        doGet(customExchange);
    }

    private void doPost(CustomHttpExchange exchange) {
        try {
            var body = exchange.getBody();
            var transacao = mapper.map(body);
            var cliente = persistence.inserirTransacao(transacao);
            var json = mapper.map(cliente);
            exchange.addHeader("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.setBody(json);
            exchange.close();
        } catch (Exception e) {
            exchange.sendResponseHeaders(422, 0);
            exchange.close();
        }
    }

    private void doGet(CustomHttpExchange exchange) {
        var path = exchange.getPath();
        var split = path.split("/");
        var id = split[1];

        var cliente = persistence.getCliente(id);
        var transacoes = persistence.findTransacoes(id);

        var saldo = new ExtratoSaldo(
                cliente.getSaldo(),
                LocalDateTime.now(),
                cliente.getLimite()
        );

        var extrato = new Extrato(
                saldo,
                transacoes
        );
        var json = mapper.map(extrato);

        exchange.addHeader("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.length());
        exchange.setBody(json);
        exchange.close();
    }

}
