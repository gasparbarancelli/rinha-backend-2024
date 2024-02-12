package io.github.gasparbarancelli;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransacaoRequisicao(
        int valor,
        TipoTransacao tipo,
        String descricao
) {

    public Transacao geraTransacao(int cliente) {
        return new Transacao(
                cliente,
                valor,
                tipo,
                descricao
        );
    }

}
