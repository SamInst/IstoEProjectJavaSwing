package repository;

import config.PostgresDatabaseConnect;
import org.springframework.transaction.annotation.Transactional;
import request.PernoiteRequest;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

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
            // 1. Inserir o pernoite e obter o ID
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

                    // 2. Inserir as diárias em batch e obter os IDs gerados
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
                        diariaStmt.executeBatch(); // Executa o lote de inserções das diárias

                        // 3. Obter os IDs das diárias e inserir as pessoas associadas
                        try (ResultSet generatedKeysDiarias = diariaStmt.getGeneratedKeys()) {
                            try (PreparedStatement pessoaStmt = connection.prepareStatement(pessoa_diaria_sql)) {
                                while (generatedKeysDiarias.next()) {
                                    long diariaId = generatedKeysDiarias.getLong(1);  // Acessar a chave gerada para cada diária

                                    for (Long pessoaId : request.pessoas()) {
                                        pessoaStmt.setLong(1, diariaId);  // ID da diária gerada
                                        pessoaStmt.setLong(2, pessoaId);  // ID da pessoa
                                        pessoaStmt.addBatch();
                                    }
                                }
                                pessoaStmt.executeBatch();  // Executa o lote de inserções
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



}
