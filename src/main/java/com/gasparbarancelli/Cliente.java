package com.gasparbarancelli;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cliente {

    private int limite;
    private int saldo;

    public Cliente(int limite) {
        this.limite = limite;
    }

    public int getLimite() {
        return limite;
    }

    public int getSaldo() {
        return saldo;
    }

    public Cliente(int limite, int saldo) {
        this.limite = limite;
        this.saldo = saldo;
    }

    public void atualizaSaldo(int valor, String tipoTransacao) {
        if ("d".equals(tipoTransacao)) {
            saldo = saldo - valor;
        } else {
            saldo = saldo + valor;
        }
    }

    @JsonIgnore
    public int getSaldoComLimite() {
        return saldo + limite;
    }

    public String[] toCSV() {
        return new String[]{
                String.valueOf(limite),
                String.valueOf(saldo)
        };
    }

    public static Cliente fromCSV(String[] csv) {
        return new Cliente(
                Integer.parseInt(csv[0]),
                Integer.parseInt(csv[1])
        );
    }

}
