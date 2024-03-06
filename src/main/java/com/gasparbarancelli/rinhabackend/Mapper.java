package com.gasparbarancelli.rinhabackend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Mapper {

    private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    public String map(String json, int clienteId) throws Exception {
        json = json.replaceAll("[{}\"]", "");
        String[] keyValuePairs = json.split(",");
        Map<String, String> params = new HashMap<>(3);
        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].trim();
            String value = entry[1].trim();
            params.put(key, value);
        }

        if (!Valida.valor.test(params.get("valor"))
                || !Valida.tipo.test(params.get("tipo"))
                || !Valida.descricao.test(params.get("descricao"))) {
            throw new Exception("Campos invalidos");
        }

        return """
                {
                  "cliente": "%d",
                  "valor": %d,
                  "tipo": "%s",
                  "descricao": "%s",
                  "data": "%s"
                }
                """.formatted(
                        clienteId,
                        Integer.parseInt(params.get("valor")),
                        TipoTransacao.valueOf(params.get("tipo")),
                        params.get("descricao"),
                        DATETIME_FORMATTER.format(LocalDateTime.now())
                );
    }

}
