package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarReservasRequest;
import request.AtualizarReservaRequest;
import request.BuscaReservasResponse;
import response.DatasReserva;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReservasRepository {
    Connection connection = PostgresDatabaseConnect.connect();

    public void adicionarReserva(AdicionarReservasRequest request) {
        String sql_reserva = """
                insert into reservas (quarto_id, data_entrada, data_saida, quantidade_pessoas, ativa) VALUES (?,?,?,?, true) returning reserva_id;
                """;

        String sql_reserva_pessoas = """
                insert into reserva_pessoas (reserva_id, pessoa_id) VALUES (?,?);
                """;

        String sql_reserva_pagamentos = """
                insert into reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento) VALUES (?,?,?,?);
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql_reserva)) {
            statement.setLong(1, request.quarto());
            statement.setDate(2, Date.valueOf(request.data_entrada()));
            statement.setDate(3, Date.valueOf(request.data_saida()));
            statement.setInt(4, request.quantidade_pessoas());

            ResultSet generatedKeys = statement.executeQuery();

            if (generatedKeys.next()) {
                long reserva_id = generatedKeys.getLong(1);

                if (request.pessoas() != null) {
                    request.pessoas().forEach(pessoa_id -> {
                        try (PreparedStatement pessoaStatement = connection.prepareStatement(sql_reserva_pessoas)) {
                            pessoaStatement.setLong(1, reserva_id);
                            pessoaStatement.setLong(2, pessoa_id);
                            pessoaStatement.executeUpdate();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                }

                if (request.pagamentos() != null) {
                    request.pagamentos().forEach(pagamento -> {
                        try (PreparedStatement pagamentoStatement = connection.prepareStatement(sql_reserva_pagamentos)) {
                            pagamentoStatement.setLong(1, reserva_id);
                            pagamentoStatement.setFloat(2, pagamento.valor_pagamento());
                            pagamentoStatement.setDate(3, Date.valueOf(LocalDate.now()));
                            pagamentoStatement.setString(4, pagamento.tipo_pagamento());
                            pagamentoStatement.executeUpdate();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public List<DatasReserva> datasReservadasPorQuarto(Long quartoId, Long reservaIdExcluida) {
        List<DatasReserva> listaDatasReservadas = new ArrayList<>();
        String sql = """
                select r.data_entrada, r.data_saida
                from reservas r
                where r.data_entrada >= now()
                  and r.ativa = true
                  and r.quarto_id = ?
                  and r.reserva_id <> ?
                order by r.data_entrada;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, quartoId);
            stmt.setLong(2, reservaIdExcluida);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DatasReserva datasReserva = new DatasReserva(
                        rs.getDate("data_entrada").toLocalDate(),
                        rs.getDate("data_saida").toLocalDate()
                );
                listaDatasReservadas.add(datasReserva);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaDatasReservadas;
    }


    public List<BuscaReservasResponse> buscaReservasAtivas() {
        List<BuscaReservasResponse> listaReservas = new ArrayList<>();

        String sql_reservas = """
                select distinct *
                from reservas r where r.ativa = true
                order by r.data_entrada;
                """;

        String sql_pessoas = """
                select
                    p.id       as pessoa_id,
                    p.nome     as nome,
                    p.telefone as telefone
                from reserva_pessoas
                left join pessoa p on p.id = reserva_pessoas.pessoa_id
                where reserva_id = ?;
                """;

        String sql_pagamentos = """
                select
                    valor,
                    data_hora_pagamento,
                    tipo_pagamento,
                    descricao
                from reserva_pagamento where reserva_id = ?;
                """;

        try (PreparedStatement reservaStmt = connection.prepareStatement(sql_reservas)) {
            ResultSet rsReserva = reservaStmt.executeQuery();

            while (rsReserva.next()) {
                long reservaId = rsReserva.getLong("reserva_id");

                List<BuscaReservasResponse.Pessoas> listaPessoas = new ArrayList<>();
                try (PreparedStatement pessoasStmt = connection.prepareStatement(sql_pessoas)) {
                    pessoasStmt.setLong(1, reservaId);
                    ResultSet rsPessoas = pessoasStmt.executeQuery();

                    while (rsPessoas.next()) {
                        listaPessoas.add(new BuscaReservasResponse.Pessoas(
                                rsPessoas.getLong("pessoa_id"),
                                rsPessoas.getString("nome"),
                                rsPessoas.getString("telefone")
                        ));
                    }
                }

                List<BuscaReservasResponse.Pagamentos> pagamentos = new ArrayList<>();
                try (PreparedStatement pagamentoStmt = connection.prepareStatement(sql_pagamentos)) {
                    pagamentoStmt.setLong(1, reservaId);
                    ResultSet rsPagamentos = pagamentoStmt.executeQuery();

                    while (rsPagamentos.next()) {
                        pagamentos.add(new BuscaReservasResponse.Pagamentos(
                                rsPagamentos.getString("descricao"),
                                rsPagamentos.getString("tipo_pagamento"),
                                rsPagamentos.getFloat("valor"),
                                rsPagamentos.getTimestamp("data_hora_pagamento").toLocalDateTime()
                        ));
                    }
                }

                listaReservas.add(new BuscaReservasResponse(
                        reservaId,
                        rsReserva.getLong("quarto_id"),
                        rsReserva.getDate("data_entrada").toLocalDate(),
                        rsReserva.getDate("data_saida").toLocalDate(),
                        rsReserva.getTime("hora_prevista").toLocalTime(),
                        listaPessoas,
                        pagamentos
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaReservas;
    }

    public void desativarReserva(long reservaId) {
        String sql = """
                    UPDATE reservas
                    SET ativa = false
                    WHERE reserva_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reservaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editarReserva(AtualizarReservaRequest request) {
        String sql_update_reserva = """
                UPDATE reservas
                SET quarto_id = ?,
                    data_entrada = ?,
                    data_saida = ?,
                    quantidade_pessoas = ?
                WHERE reserva_id = ?;
                """;
        String sql_insert_reserva_pessoas = "INSERT INTO reserva_pessoas (reserva_id, pessoa_id) VALUES (?, ?);";
        String sql_select_pessoas = "SELECT pessoa_id FROM reserva_pessoas WHERE reserva_id = ?;";
        String sql_delete_reserva_pagamentos = "DELETE FROM reserva_pagamento WHERE reserva_id = ?;";
        String sql_insert_reserva_pagamentos = """
                INSERT INTO reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento)
                VALUES (?, ?, ?, ?);
                """;

        try {
            try (PreparedStatement updateStmt = connection.prepareStatement(sql_update_reserva)) {
                updateStmt.setLong(1, request.quarto());
                updateStmt.setDate(2, Date.valueOf(request.data_entrada()));
                updateStmt.setDate(3, Date.valueOf(request.data_saida()));
                updateStmt.setInt(4, request.quantidade_pessoas());
                updateStmt.setLong(5, request.reserva_id());
                updateStmt.executeUpdate();
            }
            if (request.pessoas() != null) {
                Set<Long> pessoasExistentes = new HashSet<>();
                try (PreparedStatement selectPessoasStmt = connection.prepareStatement(sql_select_pessoas)) {
                    selectPessoasStmt.setLong(1, request.reserva_id());
                    try (ResultSet rs = selectPessoasStmt.executeQuery()) {
                        while (rs.next()) {
                            pessoasExistentes.add(rs.getLong("pessoa_id"));
                        }
                    }
                }
                for (Long pessoaId : request.pessoas()) {
                    if (!pessoasExistentes.contains(pessoaId)) {
                        try (PreparedStatement insertPessoaStmt = connection.prepareStatement(sql_insert_reserva_pessoas)) {
                            insertPessoaStmt.setLong(1, request.reserva_id());
                            insertPessoaStmt.setLong(2, pessoaId);
                            insertPessoaStmt.executeUpdate();
                        }
                    }
                }
            }
            try (PreparedStatement deletePagamentosStmt = connection.prepareStatement(sql_delete_reserva_pagamentos)) {
                deletePagamentosStmt.setLong(1, request.reserva_id());
                deletePagamentosStmt.executeUpdate();
            }
            if (request.pagamentos() != null) {
                for (BuscaReservasResponse.Pagamentos pagamento : request.pagamentos()) {
                    try (PreparedStatement insertPagamentoStmt = connection.prepareStatement(sql_insert_reserva_pagamentos)) {
                        insertPagamentoStmt.setLong(1, request.reserva_id());
                        insertPagamentoStmt.setFloat(2, pagamento.valor_pagamento());
                        insertPagamentoStmt.setTimestamp(3, Timestamp.valueOf(pagamento.data_hora_pagamento()));
                        insertPagamentoStmt.setString(4, pagamento.tipo_pagamento());
                        insertPagamentoStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar a reserva: " + e.getMessage(), e);
        }
    }

    public void removerPessoaReserva(Long idPessoa, Long idReserva) {
        String sql = "DELETE FROM reserva_pessoas WHERE pessoa_id = ? AND reserva_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idPessoa);
            stmt.setLong(2, idReserva);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao remover pessoa da reserva: " + e.getMessage(), e);
        }
    }


}
