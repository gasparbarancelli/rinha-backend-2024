package com.gasparbarancelli;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Extrato(
        ExtratoSaldo saldo,
        @JsonProperty("ultimas_transacoes")
        List<ExtratoTransacao> transacoes) {

}
