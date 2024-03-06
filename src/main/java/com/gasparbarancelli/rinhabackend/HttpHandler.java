package com.gasparbarancelli.rinhabackend;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpHandler implements com.sun.net.httpserver.HttpHandler {

    private final DataSource dataSource = new DataSource();
    private final Mapper mapper = new Mapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var customExchange = new CustomHttpExchange(exchange);

        var path = customExchange.getPath();

        var split = path.split("/");
        var id = Integer.parseInt(split[2]);

        if (id < 1 || id > 5) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }

        if (customExchange.isPost()) {
            doPost(customExchange, id);
            return;
        }

        doGet(customExchange, id);
    }

    private void doPost(CustomHttpExchange exchange, int clienteId) {
        try {
            var body = exchange.getBody();
            body = mapper.map(body, clienteId);
            var json = dataSource.insert(body);

            exchange.addHeader("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.setBody(json);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(422, 0);
        } finally {
            exchange.close();
        }
    }

    private void doGet(CustomHttpExchange exchange, int clienteId) {
        try {
            var json = dataSource.extrato(clienteId);

            exchange.addHeader("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.length());
            exchange.setBody(json);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }

}
