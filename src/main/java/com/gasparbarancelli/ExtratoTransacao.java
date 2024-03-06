package com.gasparbarancelli;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtratoTransacao(
            int valor,
            String tipo,
            String descricao,
            @JsonProperty("realizada_em")
            String data) {

    public static ExtratoTransacao fromCSV(String[] csv) {
        return new ExtratoTransacao(
                Integer.parseInt(csv[0]),
                csv[1],
                csv[2],
                csv[3]
        );
    }

}