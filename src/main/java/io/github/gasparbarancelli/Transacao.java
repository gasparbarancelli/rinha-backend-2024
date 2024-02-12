package io.github.gasparbarancelli;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACAO")
@RegisterForReflection
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CLIENTE_ID")
    private Integer cliente;

    private Integer valor;

    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    private String descricao;

    private LocalDateTime data;

    public Integer getId() {
        return id;
    }

    public Transacao() {
    }

    public Transacao(Integer cliente, Integer valor, TipoTransacao tipo, String descricao) {
        this.cliente = cliente;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.data = LocalDateTime.now();
    }

    public Integer getCliente() {
        return cliente;
    }

    public Integer getValor() {
        return valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getData() {
        return data;
    }

}
