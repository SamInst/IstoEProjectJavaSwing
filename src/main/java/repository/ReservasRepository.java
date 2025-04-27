package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarReservasRequest;
import request.AtualizarReservaRequest;
import request.BuscaReservasResponse;
import response.DatasReserva;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservasRepository {
    private static final Logger LOGGER = Logger.getLogger(ReservasRepository.class.getName());
    private final Connection connection;

    private static final String SQL_BUSCA_RESERVA_POR_ID = """
            SELECT r.reserva_id, r.quarto_id, r.data_entrada, r.data_saida, 
                   r.hora_prevista
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
            insert into reservas (quarto_id, data_entrada, data_saida, quantidade_pessoas, ativa) VALUES (?,?,?,?, true) returning reserva_id;
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

    /**
     * Busca uma reserva por ID
     *
     * @param reservaId ID da reserva
     * @return Objeto BuscaReservasResponse com os dados da reserva ou null se não encontrar
     */
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

                    List<BuscaReservasResponse.Pessoas> pessoas = buscarPessoasPorReserva(id);
                    List<BuscaReservasResponse.Pagamentos> pagamentos = buscarPagamentosPorReserva(id);

                    return new BuscaReservasResponse(
                            id, quartoId, dataEntrada, dataSaida, horaPrevista, pessoas, pagamentos
                    );
                }
            }

            return null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar reserva por ID: " + reservaId, e);
            throw new RuntimeException("Erro ao buscar reserva por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Adiciona uma nova reserva
     *
     * @param request Objeto com os dados da reserva
     */
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
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
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

    /**
     * Busca as datas reservadas para um quarto específico
     *
     * @param quartoId          ID do quarto
     * @param reservaIdExcluida ID da reserva a ser excluída da busca
     * @return Lista de objetos DatasReserva
     */
    public List<DatasReserva> datasReservadasPorQuarto(Long quartoId, Long reservaIdExcluida) {
        List<DatasReserva> listaDatasReservadas = new ArrayList<>();
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
                    DatasReserva datasReserva = new DatasReserva(
                            rs.getDate("data_entrada").toLocalDate(),
                            rs.getDate("data_saida").toLocalDate()
                    );
                    listaDatasReservadas.add(datasReserva);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar datas reservadas por quarto: " + quartoId, e);
            throw new RuntimeException("Erro ao buscar datas reservadas: " + e.getMessage(), e);
        }
        return listaDatasReservadas;
    }

    /**
     * Busca todas as reservas ativas
     *
     * @return Lista de objetos BuscaReservasResponse
     */
    public List<BuscaReservasResponse> buscaReservasAtivas() {
        List<BuscaReservasResponse> listaReservas = new ArrayList<>();

        String sql_reservas = """
                SELECT r.reserva_id, r.quarto_id, r.data_entrada, r.data_saida, r.hora_prevista
                FROM reservas r 
                WHERE r.ativa = true
                ORDER BY r.data_entrada;
                """;

        try (PreparedStatement reservaStmt = connection.prepareStatement(sql_reservas)) {
            try (ResultSet rsReserva = reservaStmt.executeQuery()) {
                while (rsReserva.next()) {
                    long reservaId = rsReserva.getLong("reserva_id");

                    List<BuscaReservasResponse.Pessoas> listaPessoas = buscarPessoasPorReserva(reservaId);
                    List<BuscaReservasResponse.Pagamentos> pagamentos = buscarPagamentosPorReserva(reservaId);

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
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar reservas ativas", e);
            throw new RuntimeException("Erro ao buscar reservas ativas: " + e.getMessage(), e);
        }

        return listaReservas;
    }

    /**
     * Desativa uma reserva
     *
     * @param reservaId ID da reserva a ser desativada
     */
    public void desativarReserva(long reservaId) {
        String sql = """
                UPDATE reservas
                SET ativa = false
                WHERE reserva_id = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reservaId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.warning("Nenhuma reserva foi desativada para o ID: " + reservaId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao desativar reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao desativar reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Remove uma pessoa de uma reserva
     *
     * @param idPessoa  ID da pessoa
     * @param idReserva ID da reserva
     */
    public void removerPessoaReserva(Long idPessoa, Long idReserva) {
        String sql = "DELETE FROM reserva_pessoas WHERE pessoa_id = ? AND reserva_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idPessoa);
            stmt.setLong(2, idReserva);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.warning("Nenhuma pessoa foi removida da reserva. ID Pessoa: " + idPessoa + ", ID Reserva: " + idReserva);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao remover pessoa da reserva. ID Pessoa: " + idPessoa + ", ID Reserva: " + idReserva, e);
            throw new RuntimeException("Erro ao remover pessoa da reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Adiciona um pagamento a uma reserva
     *
     * @param reservaId ID da reserva
     * @param pagamento Objeto com os dados do pagamento
     */
    public void adicionarPagamentoReserva(Long reservaId, BuscaReservasResponse.Pagamentos pagamento) {
        String sql_insert_pagamento = """
                INSERT INTO reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento, descricao)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql_insert_pagamento)) {
            stmt.setLong(1, reservaId);
            stmt.setFloat(2, pagamento.valor_pagamento());
            stmt.setTimestamp(3, Timestamp.valueOf(pagamento.data_hora_pagamento()));
            stmt.setInt(4, pagamento.tipo_pagamento());
            stmt.setString(5, pagamento.descricao());
            stmt.executeUpdate();
            LOGGER.info("Adicionando Pagamento: " + reservaId + " : " + pagamento.valor_pagamento());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao adicionar pagamento na reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao adicionar pagamento na reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Remove um pagamento de uma reserva
     *
     * @param reservaId          ID da reserva
     * @param descricaoPagamento Descrição do pagamento a ser removido
     */
    public void removerPagamentoReserva(Long reservaId, String descricaoPagamento) {
        String sql_delete_pagamento = """
                DELETE FROM reserva_pagamento
                WHERE reserva_id = ? AND descricao = ?;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql_delete_pagamento)) {
            stmt.setLong(1, reservaId);
            stmt.setString(2, descricaoPagamento);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.warning("Nenhum pagamento foi removido da reserva. ID Reserva: " + reservaId + ", Descrição: " + descricaoPagamento);
            } else {
                LOGGER.info("Removendo Pagamento: " + reservaId + " : " + descricaoPagamento);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao remover pagamento da reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao remover pagamento da reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Adiciona uma pessoa a uma reserva
     *
     * @param reservaId ID da reserva
     * @param pessoaId  ID da pessoa
     */
    public void adicionarPessoaReserva(Long reservaId, Long pessoaId) {
        // Verificar se a pessoa já está na reserva
        if (verificarPessoaExisteEmReserva(reservaId, pessoaId)) {
            LOGGER.info("Pessoa já vinculada a esta reserva. ID Pessoa: " + pessoaId + ", ID Reserva: " + reservaId);
            return;
        }

        String insertSql = "INSERT INTO reserva_pessoas (reserva_id, pessoa_id, representante) VALUES (?, ?, false);";

        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setLong(1, reservaId);
            insertStmt.setLong(2, pessoaId);
            insertStmt.executeUpdate();
            LOGGER.info("Pessoa adicionada à reserva: " + reservaId + " - Pessoa: " + pessoaId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao adicionar pessoa à reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao adicionar pessoa à reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se uma pessoa já está em uma reserva
     *
     * @param reservaId ID da reserva
     * @param pessoaId  ID da pessoa
     * @return true se a pessoa já estiver na reserva, false caso contrário
     */
    private boolean verificarPessoaExisteEmReserva(Long reservaId, Long pessoaId) {
        String checkSql = "SELECT COUNT(*) FROM reserva_pessoas WHERE reserva_id = ? AND pessoa_id = ?;";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setLong(1, reservaId);
            checkStmt.setLong(2, pessoaId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar se pessoa existe na reserva", e);
            throw new RuntimeException("Erro ao verificar se a pessoa já está na reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Busca os pagamentos de uma reserva
     *
     * @param reservaId ID da reserva
     * @return Lista de objetos Pagamentos
     */
    public List<BuscaReservasResponse.Pagamentos> buscarPagamentosPorReserva(Long reservaId) {
        List<BuscaReservasResponse.Pagamentos> pagamentos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(SQL_BUSCA_PAGAMENTOS_POR_RESERVA)) {
            stmt.setLong(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pagamentos.add(new BuscaReservasResponse.Pagamentos(
                            rs.getString("descricao"),
                            rs.getInt("tipo_pagamento"),
                            rs.getFloat("valor"),
                            rs.getTimestamp("data_hora_pagamento").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar pagamentos da reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao buscar pagamentos da reserva: " + e.getMessage(), e);
        }

        return pagamentos;
    }

    /**
     * Busca as pessoas de uma reserva
     *
     * @param reservaId ID da reserva
     * @return Lista de objetos Pessoas
     */
    public List<BuscaReservasResponse.Pessoas> buscarPessoasPorReserva(Long reservaId) {
        List<BuscaReservasResponse.Pessoas> pessoas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(SQL_BUSCA_PESSOAS_POR_RESERVA)) {
            stmt.setLong(1, reservaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pessoas.add(new BuscaReservasResponse.Pessoas(
                            rs.getLong("pessoa_id"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getBoolean("representante")
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar pessoas da reserva: " + reservaId, e);
            throw new RuntimeException("Erro ao buscar pessoas da reserva: " + e.getMessage(), e);
        }

        return pessoas;
    }

    /**
     * Define o representante de uma reserva
     *
     * @param reservaId     ID da reserva
     * @param pessoaID      ID da pessoa
     * @param representante true para definir como representante, false para remover
     * @throws SQLException caso ocorra algum erro no banco de dados
     */
    public void definirRepresentanteDaReserva(Long reservaId, Long pessoaID, boolean representante) throws SQLException {
        LOGGER.info(reservaId + " : " + pessoaID + " : " + representante);
        String sql = "UPDATE reserva_pessoas SET representante = ? WHERE reserva_id = ? AND pessoa_id = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, representante);
            stmt.setLong(2, reservaId);
            stmt.setLong(3, pessoaID);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Não foi possível definir o representante");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao definir representante da reserva", e);
            throw e;
        }
    }

    /**
     * Atualiza o quarto de uma reserva
     *
     * @param reservaId  ID da reserva
     * @param novoQuarto ID do novo quarto
     */
    public void atualizarQuarto(long reservaId, long novoQuarto) {
        String sql = "UPDATE reservas SET quarto_id = ? WHERE reserva_id = ?;";
        executeUpdate(sql, novoQuarto, reservaId, "Erro ao atualizar quarto da reserva");
    }

    /**
     * Atualiza a data de entrada de uma reserva
     *
     * @param reservaId   ID da reserva
     * @param novaEntrada Nova data de entrada
     */
    public void atualizarDataEntrada(long reservaId, LocalDate novaEntrada) {
        String sql = "UPDATE reservas SET data_entrada = ? WHERE reserva_id = ?;";
        executeUpdate(sql, Date.valueOf(novaEntrada), reservaId, "Erro ao atualizar data de entrada da reserva");
    }

    /**
     * Atualiza a data de saída de uma reserva
     *
     * @param reservaId ID da reserva
     * @param novaSaida Nova data de saída
     */
    public void atualizarDataSaida(long reservaId, LocalDate novaSaida) {
        String sql = "UPDATE reservas SET data_saida = ? WHERE reserva_id = ?;";
        executeUpdate(sql, Date.valueOf(novaSaida), reservaId, "Erro ao atualizar data de saída da reserva");
    }

    /**
     * Atualiza o horário previsto de uma reserva
     *
     * @param reservaId   ID da reserva
     * @param novoHorario Novo horário previsto
     */
    public void atualizarHorarioPrevisto(long reservaId, LocalTime novoHorario) {
        String sql = "UPDATE reservas SET hora_prevista = ? WHERE reserva_id = ?;";
        executeUpdate(sql, Time.valueOf(novoHorario), reservaId, "Erro ao atualizar horário previsto da reserva");
    }

    /**
     * Executa um update no banco de dados
     *
     * @param sql          Query SQL
     * @param value        Valor a ser atualizado
     * @param reservaId    ID da reserva
     * @param errorMessage Mensagem de erro
     */
    private void executeUpdate(String sql, Object value, long reservaId, String errorMessage) {
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if (value instanceof Date) {
                st.setDate(1, (Date) value);
            } else if (value instanceof Time) {
                st.setTime(1, (Time) value);
            } else if (value instanceof Long) {
                st.setLong(1, (Long) value);
            } else {
                st.setObject(1, value);
            }
            st.setLong(2, reservaId);
            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0) {
                LOGGER.warning("Nenhuma linha afetada ao atualizar reserva: " + reservaId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, errorMessage + ": " + reservaId, e);
            throw new RuntimeException(errorMessage + ": " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se existe conflito de reserva para um quarto em um período
     *
     * @param quartoId             ID do quarto
     * @param checkIn              Data de check-in
     * @param checkOut             Data de check-out
     * @param reservaIdParaExcluir ID da reserva a ser excluída da verificação
     * @return true se existir conflito, false caso contrário
     */
    public boolean existeConflitoReserva(long quartoId,
                                         LocalDate checkIn,
                                         LocalDate checkOut,
                                         long reservaIdParaExcluir) {
        String sql = """
               
        SELECT COUNT(*)
        FROM reservas
        WHERE quarto_id = ?
          AND ativa = true
          AND reserva_id <> ?
          AND NOT (data_saida <= ? OR data_entrada >= ?)
                                                               
                """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setLong(1, quartoId);
            st.setLong(2, reservaIdParaExcluir);
            st.setDate(3, Date.valueOf(checkIn));
            st.setDate(4, Date.valueOf(checkOut));
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar conflito de reserva", e);
            throw new RuntimeException("Erro ao verificar conflito de reserva: " + e.getMessage(), e);
        }
    }
}