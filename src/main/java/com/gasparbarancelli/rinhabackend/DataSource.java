/*package com.gasparbarancelli.rinhabackend;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataSource {

    private final HttpConnectionPool connectionPool;
    private final Map<Integer, HttpConnectionPool> connectionPoolByCliente = new HashMap<>();

    public DataSource() {
        String url = Optional.ofNullable(System.getenv("PERSISTENCE_ENDPOINT"))
                .orElse("http://localhost:8083");
        this.connectionPool = new HttpConnectionPool(url, 10);

        for (int i = 1; i <= 5; i++) {
            var clienteUrl = url + "/" + i;
            connectionPoolByCliente.put(i, new HttpConnectionPool(clienteUrl, 10));
        }
    }

    public String extrato(int clienteId) throws Exception {
        return connectionPoolByCliente.get(clienteId).sendRequest("GET");
    }

    public String insert(String json) throws Exception {
        return connectionPool.sendRequest("POST", json);
    }
}*/
package com.gasparbarancelli.rinhabackend;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class DataSource {

    private final String url;

    public DataSource() {
        url = Optional.ofNullable(System.getenv("PERSISTENCE_ENDPOINT"))
                .orElse("http://localhost:8083");
    }

    public String extrato(int clienteId) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url + "/" + clienteId).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] inputBytes = new byte[inputStream.available()];
                inputStream.read(inputBytes);
                return new String(inputBytes);
            }
        } else {
            throw new Exception();
        }
    }

    public String insert(String json) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(json.length()));
        connection.setDoOutput(true);

        try (OutputStream outputStream  = connection.getOutputStream()) {
            outputStream.write(json.getBytes());
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] inputBytes = new byte[inputStream.available()];
                inputStream.read(inputBytes);
                return new String(inputBytes);
            }
        } else {
            throw new Exception();
        }
    }

}
