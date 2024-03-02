package com.gasparbarancelli.rinhabackend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataSource {

    private final HikariDataSource hikariDataSource;

    public DataSource() {
        var host = Optional.ofNullable(System.getenv("DATABASE_HOST"))
                .orElse("localhost");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:tcp://" + host + ":9092/rinha-backend");
        config.setUsername("sa");
        config.setPassword("");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(5);

        hikariDataSource = new HikariDataSource(config);
        init();
    }

    private void init() {
        var createDatabase = Optional.ofNullable(System.getenv("CREATE_DATABASE"))
                .map(Boolean::valueOf)
                .orElse(false);
        if (createDatabase) {
            try (var con = hikariDataSource.getConnection();
                 var statement = con.createStatement()) {
                statement.execute("""
                        CREATE TABLE public.CLIENTE (
                            ID INT PRIMARY KEY,
                            LIMITE INT,
                            SALDO INT DEFAULT 0
                        )
                        """);

                statement.execute("""
                        CREATE TABLE public.TRANSACAO (
                            ID INT AUTO_INCREMENT PRIMARY KEY,
                            CLIENTE_ID INT NOT NULL,
                            VALOR INT NOT NULL,
                            TIPO CHAR(1) NOT NULL,
                            DESCRICAO VARCHAR(10) NOT NULL,
                            DATA TIMESTAMP NOT NULL
                        )""");

                statement.execute("CREATE INDEX IDX_TRANSACAO_CLIENTE ON TRANSACAO (CLIENTE_ID ASC)");

                statement.execute("""
                        INSERT INTO public.CLIENTE (ID, LIMITE)
                        VALUES (1, 100000),
                               (2, 80000),
                               (3, 1000000),
                               (4, 10000000),
                               (5, 500000);
                        """);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ExtratoResposta extrato(int clienteId) {
        try (var con = hikariDataSource.getConnection()) {
            var cliente = getCliente(con, clienteId);
            var transacoes = getTransacoes(con, clienteId);

            var saldo = new ExtratoSaldoResposta(
                    cliente.saldo(),
                    LocalDateTime.now(),
                    cliente.limite()
            );

            return new ExtratoResposta(
                    saldo,
                    transacoes
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Cliente getCliente(Connection con, int clienteId) throws SQLException {
        String sqlClienteFindById = """
                    SELECT LIMITE, SALDO
                    FROM CLIENTE
                    WHERE id = ?
                """;
        try (var statement = con.prepareStatement(sqlClienteFindById)) {
            statement.setInt(1, clienteId);
            try (var resultSet = statement.executeQuery()) {
                resultSet.next();

                var limite = resultSet.getInt(1);
                var saldo = resultSet.getInt(2);

                return new Cliente(
                        clienteId,
                        limite,
                        saldo
                );
            }
        }
    }

    private List<ExtratoTransacaoResposta> getTransacoes(Connection con, int clienteId) throws SQLException {
        String sqlTransacaoFind = """
                    SELECT VALOR, TIPO, DESCRICAO, DATA
                    FROM TRANSACAO
                    WHERE CLIENTE_ID = ?
                    ORDER BY DATA DESC
                    LIMIT 10;
                """;
        try (var statement = con.prepareStatement(sqlTransacaoFind)) {
            statement.setInt(1, clienteId);
            try (var resultSet = statement.executeQuery()) {
                List<ExtratoTransacaoResposta> transacoes = new ArrayList<>(resultSet.getFetchSize());
                while (resultSet.next()) {
                    var valor = resultSet.getInt(1);
                    var tipo = resultSet.getString(2);
                    var descricao = resultSet.getString(3);
                    var data = resultSet.getTimestamp(4);

                    var transacao = new ExtratoTransacaoResposta(
                            valor,
                            TipoTransacao.valueOf(tipo),
                            descricao,
                            data.toLocalDateTime()
                    );
                    transacoes.add(transacao);
                }
                return transacoes;
            }
        }
    }

    public TransacaoResposta insert(Transacao transacao) throws Exception {
        try (var con = hikariDataSource.getConnection()) {
            if (transacao.ehDebito()) {
                String sqlClienteUpdateDebito = """
                        UPDATE cliente SET saldo = saldo - ? WHERE id = ? AND limite * -1 <= saldo - ?
                        """;
                try (var stmtUpdate = con.prepareStatement(sqlClienteUpdateDebito)) {
                    stmtUpdate.setInt(1, transacao.valor());
                    stmtUpdate.setInt(2, transacao.cliente());
                    stmtUpdate.setInt(3, transacao.valor());
                    var count = stmtUpdate.executeUpdate();
                    if (count == 0) {
                        throw new Exception("Cliente sem limite");
                    }
                }
            } else {
                String sqlClienteUpdateCredito = "UPDATE cliente SET saldo = saldo + ? WHERE id = ?";
                try (var stmtUpdate = con.prepareStatement(sqlClienteUpdateCredito)) {
                    stmtUpdate.setInt(1, transacao.valor());
                    stmtUpdate.setInt(2, transacao.cliente());
                    stmtUpdate.executeUpdate();
                }
            }
            String sqlInsertTransacao = """
                     INSERT INTO transacao (cliente_id, valor, tipo, descricao, data)
                        VALUES (?, ?, ?, ?, current_timestamp)
                    """;
            try (var stmtInsert = con.prepareStatement(sqlInsertTransacao)) {
                stmtInsert.setInt(1, transacao.cliente());
                stmtInsert.setInt(2, transacao.valor());
                stmtInsert.setString(3, transacao.tipo().name());
                stmtInsert.setString(4, transacao.descricao());
                stmtInsert.executeUpdate();
            }
            var cliente = getCliente(con, transacao.cliente());
            return new TransacaoResposta(
                    cliente.limite(),
                    cliente.saldo()
            );
        }
    }

}
