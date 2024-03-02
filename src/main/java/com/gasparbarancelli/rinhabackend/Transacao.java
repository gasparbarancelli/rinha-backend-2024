package com.gasparbarancelli.rinhabackend;

import java.time.LocalDateTime;

public record Transacao(
        int cliente,
        int valor,
        TipoTransacao tipo,
        String descricao,
        LocalDateTime data
) {

    public boolean ehDebito() {
        return TipoTransacao.d.equals(tipo);
    }

}
