package repository;

import config.PostgresDatabaseConnect;
import enums.StatusPagamentoEnum;
import enums.StatusPernoiteEnum;
import enums.TipoPagamentoEnum;
import org.springframework.transaction.annotation.Transactional;
import request.PernoiteRequest;
import response.DiariaResponse;
import response.PernoiteResponse;
import response.QuartoResponse;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class PernoitesRepository extends PostgresDatabaseConnect {
    PrecosRepository precosRepository = new PrecosRepository();

    @Transactional
    public Boolean adicionarPernoite(PernoiteRequest request) {
        Boolean adicionado = true;
        String pernoite_sql = """
        INSERT INTO pernoite (
            quarto_id,
            data_entrada,
            data_saida,
            quantidade_pessoa,
            status_pernoite_enum,
            valot_total,
            hora_chegada,
            ativo)
        VALUES (?, ?, ?, ?, 0, ?, now(), true)
        RETURNING id;
        """;

        String diaria_sql = """
        INSERT INTO diaria (
            data_inicio,
            data_fim,
            quantidade_pessoa,
            valor_diaria,
            pernoite_id,
            numero_diaria)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id;
        """;

        String pessoa_diaria_sql = """
        INSERT INTO diaria_hospedes (
            diaria_id,
            hospedes_id)
        VALUES (?, ?);
        """;

        try (Connection connection = connect()) {
            try (PreparedStatement pernoiteStmt = connection.prepareStatement(pernoite_sql, Statement.RETURN_GENERATED_KEYS)) {
                pernoiteStmt.setLong(1, request.quarto_id());
                pernoiteStmt.setDate(2, java.sql.Date.valueOf(request.dataEntrada()));
                pernoiteStmt.setDate(3, java.sql.Date.valueOf(request.dataSaida()));
                pernoiteStmt.setInt(4, request.quantidade_de_pessoas());
                pernoiteStmt.setFloat(5, request.total());
                pernoiteStmt.executeUpdate();

                ResultSet generatedKeys = pernoiteStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long pernoiteId = generatedKeys.getLong(1);

                    var quantidadeDias = Period.between(request.dataEntrada(), request.dataSaida()).getDays();
                    LocalDate diariaInicio = request.dataEntrada();

                    try (PreparedStatement diariaStmt = connection.prepareStatement(diaria_sql, Statement.RETURN_GENERATED_KEYS)) {
                        for (int i = 1; i <= quantidadeDias; i++) {
                            var valor_diaria = precosRepository.precoDiaria(request.quantidade_de_pessoas());
                            LocalDate diariaFim = diariaInicio.plusDays(1);

                            diariaStmt.setDate(1, java.sql.Date.valueOf(diariaInicio)); // data_inicio
                            diariaStmt.setDate(2, java.sql.Date.valueOf(diariaFim));    // data_fim
                            diariaStmt.setInt(3, request.quantidade_de_pessoas());      // quantidade_pessoas
                            diariaStmt.setDouble(4, valor_diaria);                      // valor diaria
                            diariaStmt.setLong(5, pernoiteId);                          // pernoite_id
                            diariaStmt.setInt(6, i);                                    // número da diária (diária 1, 2, 3, etc.)

                            diariaStmt.addBatch();
                            diariaInicio = diariaFim;
                        }
                        diariaStmt.executeBatch();

                        try (ResultSet generatedKeysDiarias = diariaStmt.getGeneratedKeys()) {
                            try (PreparedStatement pessoaStmt = connection.prepareStatement(pessoa_diaria_sql)) {
                                while (generatedKeysDiarias.next()) {
                                    long diariaId = generatedKeysDiarias.getLong(1);

                                    for (Long pessoaId : request.pessoas()) {
                                        pessoaStmt.setLong(1, diariaId);
                                        pessoaStmt.setLong(2, pessoaId);
                                        pessoaStmt.addBatch();
                                    }
                                }
                                pessoaStmt.executeBatch();
                            }
                        }
                    }
                } else {
                    adicionado = false;
                    throw new SQLException("Falha ao obter o ID do pernoite.");
                }
            }
        } catch (SQLException e) {
            adicionado = false;
            e.printStackTrace();
        }
        return adicionado;
    }


    public PernoiteResponse buscaPernoite(Long pernoiteId) {
        String sql_pernoite = "SELECT * FROM pernoite WHERE id = ?";
        String sql_diaria = "SELECT * FROM diaria WHERE pernoite_id = ?";
        String sql_pessoa = "SELECT p.id, p.nome, p.cpf, p.telefone FROM pessoa p " +
                "JOIN diaria_hospedes dh ON dh.hospedes_id = p.id WHERE diaria_id = ?";
        String sql_pagamentos = "SELECT * FROM diaria_pagamento WHERE diaria_id = ?";
        String sql_consumos = "SELECT * FROM consumo_diaria join item i on i.id = consumo_diaria.item_id WHERE diaria_id = ?";

        try (Connection connection = connect()) {
            // 1. Obter os dados da tabela "pernoite"
            PernoiteResponse pernoiteResponse = null;
            try (PreparedStatement pernoiteStmt = connection.prepareStatement(sql_pernoite)) {
                pernoiteStmt.setLong(1, pernoiteId);
                ResultSet rsPernoite = pernoiteStmt.executeQuery();

                if (rsPernoite.next()) {
                    // 2. Buscar as diárias associadas ao pernoite
                    List<DiariaResponse> diarias = new ArrayList<>();
                    try (PreparedStatement diariaStmt = connection.prepareStatement(sql_diaria)) {
                        diariaStmt.setLong(1, pernoiteId);
                        ResultSet rsDiaria = diariaStmt.executeQuery();

                        while (rsDiaria.next()) {
                            long diariaId = rsDiaria.getLong("id");
                            LocalDate dataEntrada = rsDiaria.getDate("data_inicio").toLocalDate();
                            LocalDate dataSaida = rsDiaria.getDate("data_fim").toLocalDate();
                            Float valorDiaria = rsDiaria.getFloat("valor_diaria");

                            // 3. Buscar as pessoas associadas à diária
                            List<DiariaResponse.Pessoa> pessoas = new ArrayList<>();
                            try (PreparedStatement pessoaStmt = connection.prepareStatement(sql_pessoa)) {
                                pessoaStmt.setLong(1, diariaId);
                                ResultSet rsPessoa = pessoaStmt.executeQuery();

                                while (rsPessoa.next()) {
                                    pessoas.add(new DiariaResponse.Pessoa(
                                            rsPessoa.getLong("id"),
                                            rsPessoa.getString("nome"),
                                            rsPessoa.getString("cpf"),
                                            rsPessoa.getString("telefone")
                                    ));
                                }
                            }

                            // 4. Buscar os pagamentos associados à diária
                            List<DiariaResponse.Pagamento> pagamentos = new ArrayList<>();
                            try (PreparedStatement pagamentoStmt = connection.prepareStatement(sql_pagamentos)) {
                                pagamentoStmt.setLong(1, diariaId);
                                ResultSet rsPagamento = pagamentoStmt.executeQuery();



                                while (rsPagamento.next()) {
                                    String tipoPagamento = switch (rsPagamento.getString("tipo_pagamento_enum")) {
                                        case "0" -> "PIX";
                                        case "1" -> "DINHEIRO";
                                        case "2" -> "CARTAO DE CREDITO";
                                        case "3" -> "CARTAO DE DEBITO";
                                        case "4" -> "TRANSFERENCIA BANCARIA";
                                        case "5" -> "CARTAO VIRTUAL";
                                        default -> "DESCONHECIDO";
                                    };

                                    String statusPAgamento = switch (rsPagamento.getString("status_pagamento_enum")){
                                        case "0" -> "PENDENTE";
                                        case "1" -> "PAGO";
                                        default -> "DESCONHECIDO";
                                    };
                                    pagamentos.add(new DiariaResponse.Pagamento(
                                            rsPagamento.getLong("id"),
                                            rsPagamento.getTimestamp("data_hora_pagamento").toLocalDateTime(),
                                            tipoPagamento,
                                            statusPAgamento,
                                            rsPagamento.getFloat("valor")
                                    ));
                                }
                            }

                            // 5. Buscar os consumos associados à diária
                            List<DiariaResponse.Consumo.Itens> itens = new ArrayList<>();
                            List<DiariaResponse.Consumo> consumos = new ArrayList<>();
                            float totalConsumo = 0f;
                            try (PreparedStatement consumoStmt = connection.prepareStatement(sql_consumos)) {
                                consumoStmt.setLong(1, diariaId);
                                ResultSet rsConsumo = consumoStmt.executeQuery();

                                while (rsConsumo.next()) {
                                    float valorItem = rsConsumo.getFloat("valor");
                                    int quantidade = rsConsumo.getInt("quantidade");
                                    totalConsumo += valorItem * quantidade;

                                    itens.add(new DiariaResponse.Consumo.Itens(
                                            rsConsumo.getTimestamp("data_hora_consumo").toLocalDateTime(),
                                            rsConsumo.getLong("item_id"),
                                            rsConsumo.getString("descricao"),
                                            valorItem
                                    ));
                                }
                            }

                            // Adicionar o consumo (agora em uma lista)
                            consumos.add(new DiariaResponse.Consumo(totalConsumo, itens));

                            // Montar a resposta da diária
                            DiariaResponse diariaResponse = new DiariaResponse(
                                    diariaId,
                                    dataEntrada,
                                    dataSaida,
                                    valorDiaria,
                                    pagamentos,
                                    consumos, // Agora uma lista de consumos
                                    pessoas
                            );
                            diarias.add(diariaResponse);
                        }
                    }

                    // Montar a resposta do pernoite
                    pernoiteResponse = new PernoiteResponse(
                            pernoiteId,
                            rsPernoite.getBoolean("ativo"),
                            rsPernoite.getLong("quarto_id"),
                            rsPernoite.getTime("hora_chegada").toLocalTime(),
                            rsPernoite.getDate("data_entrada").toLocalDate(),
                            rsPernoite.getDate("data_saida").toLocalDate(),
                            rsPernoite.getFloat("valot_total"),
                            rsPernoite.getInt("quantidade_pessoa"),
                            rsPernoite.getString("status_pernoite_enum"),
                            diarias
                    );
                }
            }

            return pernoiteResponse;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





}
