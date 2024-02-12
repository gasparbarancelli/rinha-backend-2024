package io.github.gasparbarancelli;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;


@Entity
@Table(name = "CLIENTE")
@RegisterForReflection
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer limite;

    private Integer saldo;

    public Cliente() {
    }

    public Cliente(Integer id, Integer limite) {
        this.id = id;
        this.limite = limite;
    }

    public Integer getLimite() {
        return limite;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void atualizaSaldo(Integer valor, TipoTransacao tipoTransacao) {
        if (TipoTransacao.d.equals(tipoTransacao)) {
            saldo = saldo - valor;
        } else {
            saldo = saldo + valor;
        }
    }

    public Integer getSaldoComLimite() {
        return saldo + limite;
    }

    public static boolean naoExiste(Integer id) {
        return id < 1 || id > 5;
    }

}
