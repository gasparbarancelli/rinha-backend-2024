package com.gasparbarancelli;

public record Transacao(
        String cliente,
        int valor,
        String tipo,
        String descricao,
        String data
) {

    public String[] toCSV() {
        return new String[]{
                String.valueOf(valor),
                tipo,
                descricao,
                data
        };
    }

    public static Transacao fromCSV(String cliente, String[] csv) {
        return new Transacao(
                cliente,
                Integer.parseInt(csv[0]),
                csv[1],
                csv[2],
                csv[3]
        );
    }

}
