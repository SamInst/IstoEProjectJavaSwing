package repository;

import config.PostgresDatabaseConnect;
import enums.StatusPernoiteEnum;
import enums.StatusQuartoEnum;
import org.springframework.transaction.annotation.Transactional;
import request.PernoiteRequest;
import response.BuscaPernoiteResponse;
import response.DiariaResponse;
import response.PernoiteResponse;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static principals.tools.Converter.converterStatusPagamento;
import static principals.tools.Converter.converterTipoPagamento;

public class PernoitesRepository extends PostgresDatabaseConnect {
    PrecosRepository precosRepository = new PrecosRepository();
    QuartosRepository quartosRepository = new QuartosRepository();
    Connection connection = connect();

    @Transactional
    public Boolean adicionarPernoite(PernoiteRequest request) {
        boolean adicionado = true;
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            quartosRepository.alterarStatusQuarto(request.quarto_id(), StatusQuartoEnum.OCUPADO);
        return adicionado;
    }


    public PernoiteResponse buscaPernoite(Long pernoiteId) {
        String sql_pernoite = "SELECT * FROM pernoite WHERE id = ?";

            PernoiteResponse pernoiteResponse = null;
            try (PreparedStatement pernoiteStmt = connection.prepareStatement(sql_pernoite)) {
                pernoiteStmt.setLong(1, pernoiteId);
                ResultSet rsPernoite = pernoiteStmt.executeQuery();

                if (rsPernoite.next()) {
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
                            buscaDiariasPorPernoite(pernoiteId)
                    );
                }
            } catch (SQLException e) { throw new RuntimeException(e); }
        return pernoiteResponse;
    }



    public List<BuscaPernoiteResponse> buscaPernoitesPorStatus(StatusPernoiteEnum statusPernoite) {
        String sqlPernoite = """
        SELECT
           p.id as pernoite_id,
           p.ativo,
           p.quarto_id,
           p.hora_chegada,
           p.data_entrada,
           p.data_saida,
           p.valot_total,
           p.status_pernoite_enum,
           COUNT(d.id) AS quantidade_diarias
        FROM pernoite p
        LEFT JOIN diaria d ON d.pernoite_id = p.id
        WHERE p.status_pernoite_enum = ?
        GROUP BY p.id
        """;

        String sqlQuantidadePessoas = """
        SELECT COUNT(DISTINCT dh.hospedes_id) AS quantidade_pessoas
        FROM diaria_hospedes dh
        JOIN diaria d ON dh.diaria_id = d.id
        WHERE d.pernoite_id = ?;
        
        """;

        String sqlQuantidadeConsumo = """
        SELECT COUNT(cd.id * cd.quantidade) AS quantidade_consumo
        FROM consumo_diaria cd
        JOIN diaria d ON cd.diaria_id = d.id
        WHERE d.pernoite_id = ?
        """;

        String sqlRepresentante = """
        SELECT p.id, p.nome, p.telefone
        FROM pessoa p
        JOIN diaria_hospedes dh ON p.id = dh.hospedes_id
        JOIN diaria d ON dh.diaria_id = d.id
        WHERE d.pernoite_id = ?
        LIMIT 1
        """;

        List<BuscaPernoiteResponse> pernoites = new ArrayList<>();

            try (PreparedStatement pernoiteStmt = connection.prepareStatement(sqlPernoite)) {
                pernoiteStmt.setInt(1, statusPernoite.getValue());
                ResultSet rsPernoite = pernoiteStmt.executeQuery();

                while (rsPernoite.next()) {
                    long pernoiteIdResult = rsPernoite.getLong("pernoite_id");
                    boolean ativo = rsPernoite.getBoolean("ativo");
                    long quarto = rsPernoite.getLong("quarto_id");
                    LocalTime horaChegada = rsPernoite.getTime("hora_chegada").toLocalTime();
                    LocalDate dataEntrada = rsPernoite.getDate("data_entrada").toLocalDate();
                    LocalDate dataSaida = rsPernoite.getDate("data_saida").toLocalDate();
                    float valorTotal = rsPernoite.getFloat("valot_total");
                    String statusPernoiteStr = rsPernoite.getString("status_pernoite_enum");
                    int quantidadeDiarias = rsPernoite.getInt("quantidade_diarias");

                    int quantidadePessoas = 0;
                    try (PreparedStatement pessoasStmt = connection.prepareStatement(sqlQuantidadePessoas)) {
                        pessoasStmt.setLong(1, pernoiteIdResult);
                        ResultSet rsPessoas = pessoasStmt.executeQuery();
                        if (rsPessoas.next()) {
                            quantidadePessoas = rsPessoas.getInt("quantidade_pessoas");
                        }
                    }

                    int quantidadeConsumo = 0;
                    try (PreparedStatement consumoStmt = connection.prepareStatement(sqlQuantidadeConsumo)) {
                        consumoStmt.setLong(1, pernoiteIdResult);
                        ResultSet rsConsumo = consumoStmt.executeQuery();
                        if (rsConsumo.next()) {
                            quantidadeConsumo = rsConsumo.getInt("quantidade_consumo");
                        }
                    }

                    BuscaPernoiteResponse.Representante representante = null;
                    try (PreparedStatement representanteStmt = connection.prepareStatement(sqlRepresentante)) {
                        representanteStmt.setLong(1, pernoiteIdResult);
                        ResultSet rsRepresentante = representanteStmt.executeQuery();
                        if (rsRepresentante.next()) {
                            representante = new BuscaPernoiteResponse.Representante(
                                    rsRepresentante.getLong("id"),
                                    rsRepresentante.getString("nome"),
                                    rsRepresentante.getString("telefone")
                            );
                        }
                    }

                    BuscaPernoiteResponse pernoiteResponse = new BuscaPernoiteResponse(
                            pernoiteIdResult,
                            ativo,
                            quarto,
                            horaChegada,
                            dataEntrada,
                            dataSaida,
                            valorTotal,
                            quantidadePessoas,
                            quantidadeDiarias,
                            quantidadeConsumo,
                            statusPernoiteStr,
                            representante
                    );

                    pernoites.add(pernoiteResponse);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        return pernoites.stream()
                    .sorted(Comparator.comparingLong(BuscaPernoiteResponse::quarto))
                    .toList();
    }


    public Integer hospedados() {
        String sql = """
        SELECT COUNT(DISTINCT dh.hospedes_id) AS quantidade_pessoas
        FROM diaria_hospedes dh
        JOIN diaria d ON dh.diaria_id = d.id
        JOIN pernoite p ON p.id = d.pernoite_id
        WHERE p.ativo = true;
    """;

        try (
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade_pessoas");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar a quantidade de hospedados", e);
        }

        return 0;
    }



    public List<DiariaResponse> buscaDiariasPorPernoite(Long pernoiteId){
        List<DiariaResponse> diarias = new ArrayList<>();

        String sql_diaria = "SELECT * FROM diaria WHERE pernoite_id = ?";
        String sql_pessoa = "SELECT p.id, p.nome, p.cpf, p.telefone FROM pessoa p JOIN diaria_hospedes dh ON dh.hospedes_id = p.id WHERE diaria_id = ?";
        String sql_pagamentos = "SELECT * FROM diaria_pagamento WHERE diaria_id = ?";
        String sql_consumos = "SELECT * FROM consumo_diaria join item i on i.id = consumo_diaria.item_id WHERE diaria_id = ?";

        try (PreparedStatement diariaStmt = connection.prepareStatement(sql_diaria)) {
            diariaStmt.setLong(1, pernoiteId);
            ResultSet rsDiaria = diariaStmt.executeQuery();

            while (rsDiaria.next()) {
                long diariaId = rsDiaria.getLong("id");
                LocalDate dataEntrada = rsDiaria.getDate("data_inicio").toLocalDate();
                LocalDate dataSaida = rsDiaria.getDate("data_fim").toLocalDate();
                Float valorDiaria = rsDiaria.getFloat("valor_diaria");
                Integer numero = rsDiaria.getInt("numero_diaria");

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

                List<DiariaResponse.Pagamento> pagamentos = new ArrayList<>();

                try (PreparedStatement pagamentoStmt = connection.prepareStatement(sql_pagamentos)) {
                    pagamentoStmt.setLong(1, diariaId);
                    ResultSet rsPagamento = pagamentoStmt.executeQuery();

                    while (rsPagamento.next()) {
                        pagamentos.add(new DiariaResponse.Pagamento(
                                rsPagamento.getLong("id"),
                                rsPagamento.getTimestamp("data_hora_pagamento").toLocalDateTime(),
                                converterTipoPagamento(rsPagamento.getString("status_pagamento_enum")),
                                converterStatusPagamento(rsPagamento.getString("status_pagamento_enum")),
                                rsPagamento.getFloat("valor")
                        ));
                    }
                }

                List<DiariaResponse.Consumo.Itens> itens = new ArrayList<>();

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
                                quantidade,
                                rsConsumo.getInt("categoria"),
                                rsConsumo.getString("descricao"),
                                valorItem
                        ));
                    }
                }

                DiariaResponse diariaResponse = new DiariaResponse(
                        diariaId,
                        numero,
                        dataEntrada,
                        dataSaida,
                        valorDiaria,
                        pagamentos,
                        new DiariaResponse.Consumo(totalConsumo, itens),
                        pessoas
                );
                diarias.add(diariaResponse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return diarias;
    }
}
