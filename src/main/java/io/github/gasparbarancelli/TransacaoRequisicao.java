package io.github.gasparbarancelli;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransacaoRequisicao(
        String valor,
        TipoTransacao tipo,
        String descricao
) {

    public TransacaoRequisicao {
        if (!Valida.valor.test(valor) || tipo == null || !Valida.descricao.test(descricao)) {
            throw new IllegalArgumentException();
        }
    }

    public Transacao geraTransacao(int cliente) {
        return new Transacao(
                cliente,
                Integer.parseInt(valor),
                tipo,
                descricao
        );
    }

}
