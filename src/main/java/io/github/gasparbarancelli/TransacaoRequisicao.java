package io.github.gasparbarancelli;

public record TransacaoRequisicao(
        String valor,
        TipoTransacao tipo,
        String descricao
) {

    public boolean ehValido() {
        return Valida.valor.test(valor)
                && tipo != null
                && Valida.descricao.test(descricao);
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
