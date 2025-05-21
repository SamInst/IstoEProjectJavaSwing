package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarReservasRequest;
import request.AtualizarReservaRequest;
import request.BuscaReservasResponse;
import response.DatasReserva;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReservasRepository {
    private static final Logger LOGGER = Logger.getLogger(ReservasRepository.class.getName());
    private final Connection connection;

    private static final String SQL_BUSCA_RESERVA_POR_ID = """
            SELECT r.reserva_id, r.quarto_id, r.data_entrada, r.data_saida,
                   r.hora_prevista, r.hospedado
            FROM reservas r 
            WHERE r.reserva_id = ? AND r.ativa = true
            """;

    private static final String SQL_BUSCA_PESSOAS_POR_RESERVA = """
            SELECT p.id as pessoa_id, p.nome, p.telefone, rp.representante
            FROM reserva_pessoas rp
            LEFT JOIN pessoa p ON p.id = rp.pessoa_id
            WHERE rp.reserva_id = ?
            ORDER BY rp.representante DESC, p.nome
            """;

    private static final String SQL_BUSCA_PAGAMENTOS_POR_RESERVA = """
            SELECT valor, data_hora_pagamento, tipo_pagamento, descricao
            FROM reserva_pagamento 
            WHERE reserva_id = ?
            ORDER BY data_hora_pagamento
            """;

    private static final String SQL_RESERVA = """
            insert into reservas (quarto_id, data_entrada, data_saida, quantidade_pessoas, ativa, hospedado) VALUES (?,?,?,?, true, false) returning reserva_id;
            """;

    private static final String SQL_RESERVA_PESSOAS = """
            insert into reserva_pessoas (reserva_id, pessoa_id) VALUES (?,?);
            """;

    private static final String SQL_RESERVA_PAGAMENTOS = """
            insert into reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento) VALUES (?,?,?,?);
            """;

    public ReservasRepository() {
        this.connection = PostgresDatabaseConnect.connect();
    }

    public BuscaReservasResponse buscarReservaPorId(Long reservaId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_BUSCA_RESERVA_POR_ID)) {
            statement.setLong(1, reservaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("reserva_id");
                    Long quartoId = resultSet.getLong("quarto_id");
                    LocalDate dataEntrada = resultSet.getDate("data_entrada").toLocalDate();
                    LocalDate dataSaida = resultSet.getDate("data_saida").toLocalDate();
                    LocalTime horaPrevista = resultSet.getTime("hora_prevista").toLocalTime();
                    Boolean hospedado = resultSet.getBoolean("hospedado");
                    List<BuscaReservasResponse.Pessoas> pessoas = buscarPessoasPorReserva(id);
                    List<BuscaReservasResponse.Pagamentos> pagamentos = buscarPagamentosPorReserva(id);
                    return new BuscaReservasResponse(id, quartoId, dataEntrada, dataSaida, horaPrevista, pessoas, pagamentos, hospedado);
                }
            }
            return null;
        } catch (SQLException e) {
            LOGGER.severe("Erro ao buscar reserva por ID: " + reservaId + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void adicionarReserva(AdicionarReservasRequest request) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_RESERVA)) {
            statement.setLong(1, request.quarto());
            statement.setDate(2, Date.valueOf(request.data_entrada()));
            statement.setDate(3, Date.valueOf(request.data_saida()));
            statement.setInt(4, request.quantidade_pessoas());

            ResultSet generatedKeys = statement.executeQuery();
            if (generatedKeys.next()) {
                long reserva_id = generatedKeys.getLong(1);
                if (request.pessoas() != null) {
                    request.pessoas().forEach(pessoa_id -> {
                        try (PreparedStatement pessoaStatement = connection.prepareStatement(SQL_RESERVA_PESSOAS)) {
                            pessoaStatement.setLong(1, reserva_id);
                            pessoaStatement.setLong(2, pessoa_id);
                            pessoaStatement.executeUpdate();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
                if (request.pagamentos() != null) {
                    request.pagamentos().forEach(pagamento -> {
                        try (PreparedStatement pagamentoStatement = connection.prepareStatement(SQL_RESERVA_PAGAMENTOS)) {
                            pagamentoStatement.setLong(1, reserva_id);
                            pagamentoStatement.setFloat(2, pagamento.valor_pagamento());
                            pagamentoStatement.setDate(3, Date.valueOf(LocalDate.now()));
                            pagamentoStatement.setString(4, pagamento.tipo_pagamento());
                            pagamentoStatement.executeUpdate();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DatasReserva> datasReservadasPorQuarto(Long quartoId, Long reservaIdExcluida) {
        List<DatasReserva> lista = new ArrayList<>();
        String sql = """
                SELECT r.data_entrada, r.data_saida
                FROM reservas r
                WHERE r.data_entrada >= now()
                  AND r.ativa = true
                  AND r.quarto_id = ?
                  AND r.reserva_id <> ?
                ORDER BY r.data_entrada;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, quartoId);
            stmt.setLong(2, reservaIdExcluida);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DatasReserva(rs.getDate("data_entrada").toLocalDate(), rs.getDate("data_saida").toLocalDate()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public List<BuscaReservasResponse> buscaReservasAtivas() {
        List<BuscaReservasResponse> lista = new ArrayList<>();
        String sql = """
                SELECT r.reserva_id, r.quarto_id, r.data_entrada, r.data_saida, r.hora_prevista, r.hospedado
                FROM reservas r 
                WHERE r.ativa = true
                ORDER BY r.data_entrada;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("reserva_id");
                    LocalDate entrada = rs.getDate("data_entrada").toLocalDate();
                    LocalDate saida = rs.getDate("data_saida").toLocalDate();
                    LocalTime hora = rs.getTime("hora_prevista").toLocalTime();
                    Boolean hospedado = rs.getBoolean("hospedado");
                    List<BuscaReservasResponse.Pessoas> p = buscarPessoasPorReserva(id);
                    List<BuscaReservasResponse.Pagamentos> pag = buscarPagamentosPorReserva(id);
                    lista.add(new BuscaReservasResponse(id, rs.getLong("quarto_id"), entrada, saida, hora, p, pag, hospedado));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public void desativarReserva(long reservaId) {
        String sql = "UPDATE reservas SET ativa = false WHERE reserva_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void hospedarReserva(long reservaId) {
        String sql = "UPDATE reservas SET hospedado = true WHERE reserva_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removerPessoaReserva(Long idPessoa, Long idReserva) {
        String sql = "DELETE FROM reserva_pessoas WHERE pessoa_id = ? AND reserva_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idPessoa);
            stmt.setLong(2, idReserva);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void adicionarPagamentoReserva(Long reservaId, BuscaReservasResponse.Pagamentos pagamento) {
        String sql = "INSERT INTO reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento, descricao) VALUES (?,?,?, ?,?);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.setFloat(2, pagamento.valor_pagamento());
            stmt.setTimestamp(3, Timestamp.valueOf(pagamento.data_hora_pagamento()));
            stmt.setInt(4, pagamento.tipo_pagamento());
            stmt.setString(5, pagamento.descricao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removerPagamentoReserva(Long reservaId, String descricao) {
        String sql = "DELETE FROM reserva_pagamento WHERE reserva_id = ? AND descricao = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.setString(2, descricao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void adicionarPessoaReserva(Long reservaId, Long pessoaId) {
        if (verificarPessoaExisteEmReserva(reservaId, pessoaId)) return;
        String sql = "INSERT INTO reserva_pessoas (reserva_id, pessoa_id, representante) VALUES (?, ?, false);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.setLong(2, pessoaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verificarPessoaExisteEmReserva(Long reservaId, Long pessoaId) {
        String sql = "SELECT COUNT(*) FROM reserva_pessoas WHERE reserva_id = ? AND pessoa_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, reservaId);
            stmt.setLong(2, pessoaId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BuscaReservasResponse.Pagamentos> buscarPagamentosPorReserva(Long reservaId) {
        List<BuscaReservasResponse.Pagamentos> pagamentos = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_BUSCA_PAGAMENTOS_POR_RESERVA)) {
            stmt.setLong(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagamentos.add(new BuscaReservasResponse.Pagamentos(rs.getString("descricao"), rs.getInt("tipo_pagamento"), rs.getFloat("valor"), rs.getTimestamp("data_hora_pagamento").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pagamentos;
    }

    public List<BuscaReservasResponse.Pessoas> buscarPessoasPorReserva(Long reservaId) {
        List<BuscaReservasResponse.Pessoas> pessoas = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_BUSCA_PESSOAS_POR_RESERVA)) {
            stmt.setLong(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pessoas.add(new BuscaReservasResponse.Pessoas(rs.getLong("pessoa_id"), rs.getString("nome"), rs.getString("telefone"), rs.getBoolean("representante")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pessoas;
    }

    public void definirRepresentanteDaReserva(Long reservaId, Long pessoaID, boolean representante) throws SQLException {
        String sql = "UPDATE reserva_pessoas SET representante = ? WHERE reserva_id = ? AND pessoa_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, representante);
            stmt.setLong(2, reservaId);
            stmt.setLong(3, pessoaID);
            if (stmt.executeUpdate() == 0) throw new SQLException("Não foi possível definir o representante");
        }
    }

    public void atualizarQuarto(long reservaId, long novoQuarto) { executeUpdate("UPDATE reservas SET quarto_id = ? WHERE reserva_id = ?;", novoQuarto, reservaId); }
    public void atualizarDataEntrada(long reservaId, LocalDate novaEntrada) { executeUpdate("UPDATE reservas SET data_entrada = ? WHERE reserva_id = ?;", Date.valueOf(novaEntrada), reservaId); }
    public void atualizarDataSaida(long reservaId, LocalDate novaSaida) { executeUpdate("UPDATE reservas SET data_saida = ? WHERE reserva_id = ?;", Date.valueOf(novaSaida), reservaId); }
    public void atualizarHorarioPrevisto(long reservaId, LocalTime novoHorario) { executeUpdate("UPDATE reservas SET hora_prevista = ? WHERE reserva_id = ?;", Time.valueOf(novoHorario), reservaId); }

    private void executeUpdate(String sql, Object value, long reservaId) {
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if      (value instanceof Date) st.setDate(1, (Date) value);
            else if (value instanceof Time) st.setTime(1, (Time) value);
            else if (value instanceof Long) st.setLong(1, (Long) value);
            else                            st.setObject(1, value);
            st.setLong(2, reservaId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existeConflitoReserva(long quartoId, LocalDate checkIn, LocalDate checkOut, long reservaIdParaExcluir) {
        String sql = """
                SELECT COUNT(*)
                FROM reservas
                WHERE quarto_id = ?
                  AND ativa = true
                  AND reserva_id <> ?
                  AND (
                      (data_entrada < ? AND data_saida > ?)
                      OR (data_entrada >= ? AND data_entrada < ?)
                      OR (data_saida > ? AND data_saida <= ?)
                  )
                """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, quartoId); st.setLong(2, reservaIdParaExcluir);
            st.setDate(3, Date.valueOf(checkOut)); st.setDate(4, Date.valueOf(checkIn));
            st.setDate(5, Date.valueOf(checkIn)); st.setDate(6, Date.valueOf(checkOut));
            st.setDate(7, Date.valueOf(checkIn)); st.setDate(8, Date.valueOf(checkOut));
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public record OcupacaoDia(int ocupados, int total, int percentual) {}

    public static OcupacaoDia buscarOcupacaoPorDia(LocalDate dia) {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM quarto) AS total_quartos,
                    (SELECT COUNT(*)
                     FROM reservas
                     WHERE ativa = true
                     AND data_entrada <= ?
                     AND data_saida > ?) AS ocupados
                """;
        try (Connection conn = PostgresDatabaseConnect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dia)); stmt.setDate(2, Date.valueOf(dia));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new OcupacaoDia(rs.getInt("ocupados"), rs.getInt("total_quartos"), rs.getInt("ocupados") * 100 / rs.getInt("total_quartos"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new OcupacaoDia(0, 0, 0);
    }

    public static int buscarReservasHospedadasPorDia(LocalDate dia) {
        String sql = """
                SELECT COUNT(*) AS reservasHospedadas
                FROM reservas
                WHERE ativa = true
                AND hospedado = true
                AND data_entrada <= ?
                AND data_saida > ?
            """;
        try (Connection conn = PostgresDatabaseConnect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dia));
            stmt.setDate(2, Date.valueOf(dia));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("reservasHospedadas");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public boolean podeMoverReserva(long novoQuartoId, LocalDate checkIn, LocalDate checkOut, long reservaIdParaExcluir) {
        return !existeConflitoReserva(novoQuartoId, checkIn, checkOut, reservaIdParaExcluir);
    }

    public static int contarTotalPessoasPorData(LocalDate data) {
        String sql = """
            SELECT SUM(r.quantidade_pessoas) as total_pessoas
            FROM reservas r
            WHERE r.data_entrada <= ?
            AND r.data_saida > ?
            """;
        try (Connection conn = PostgresDatabaseConnect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(data));
            stmt.setDate(2, Date.valueOf(data));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_pessoas");
                    return rs.wasNull() ? 0 : total;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erro ao contar total de pessoas por data: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static int contarPessoasReservasAtivasPorData(LocalDate data) {
        String sql = """
            SELECT count(p.id)  as total_pessoas
            FROM reservas r
            join public.reserva_pessoas rp on r.reserva_id = rp.reserva_id
            join public.pessoa p on p.id = rp.pessoa_id
            WHERE r.ativa = true
            AND r.data_entrada <= ?
            AND r.data_saida > ?
            """;
        try (Connection conn = PostgresDatabaseConnect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(data));
            stmt.setDate(2, Date.valueOf(data));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_pessoas");
                    return rs.wasNull() ? 0 : total;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erro ao contar pessoas com reservas ativas por data: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static int contarPessoasHospedadasPorData(LocalDate data) {
        String sql = """
            SELECT count(p.id) as total_pessoas
            FROM reservas r
            join public.reserva_pessoas rp on r.reserva_id = rp.reserva_id
            join public.pessoa p on p.id = rp.pessoa_id
            WHERE r.ativa = true
            AND r.hospedado = true
            AND r.data_entrada <= ?
            AND r.data_saida > ?
            """;
        try (Connection conn = PostgresDatabaseConnect.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(data));
            stmt.setDate(2, Date.valueOf(data));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_pessoas");
                    return rs.wasNull() ? 0 : total;
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Erro ao contar pessoas hospedadas por data: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return 0;
    }

//    public static int contarPessoasReaisPorData(LocalDate data, boolean apenasHospedadas, boolean apenasAtivas) {
//        StringBuilder sql = new StringBuilder("""
//            SELECT COUNT(rp.pessoa_id) as total_pessoas
//            FROM reserva_pessoas rp
//            JOIN reservas r ON r.reserva_id = rp.reserva_id
//            WHERE r.data_entrada <= ?
//            AND r.data_saida > ?
//            """);
//
//        if (apenasAtivas) {
//            sql.append("AND r.ativa = true ");
//        }
//
//        if (apenasHospedadas) {
//            sql.append("AND r.hospedado = true ");
//        }
//
//        try (Connection conn = PostgresDatabaseConnect.connect();
//             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
//            stmt.setDate(1, Date.valueOf(data));
//            stmt.setDate(2, Date.valueOf(data));
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    int total = rs.getInt("total_pessoas");
//                    return rs.wasNull() ? 0 : total;
//                }
//            }
//        } catch (SQLException e) {
//            LOGGER.severe("Erro ao contar pessoas reais por data: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//        return 0;
//    }
}
