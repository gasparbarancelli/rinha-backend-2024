package io.github.gasparbarancelli;

public record TransacaoRequisicao(
        int valor,
        TipoTransacao tipo,
        String descricao
) {

    public boolean ehValido() {
        return valor > 0
                && tipo != null
                && descricao != null
                && !descricao.isEmpty()
                && descricao.length() <= 10;
    }

    public Transacao geraTransacao(int cliente) {
        return new Transacao(
                cliente,
                valor,
                tipo,
                descricao
        );
    }

}
