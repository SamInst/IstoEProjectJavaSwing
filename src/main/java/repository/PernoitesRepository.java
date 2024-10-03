package repository;

import config.PostgresDatabaseConnect;
import request.PernoiteRequest;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class PernoitesRepository extends PostgresDatabaseConnect {

    PrecosRepository precosRepository = new PrecosRepository();

    public void adicionarPernoite(PernoiteRequest request) {
        String pernoite_sql = """
            INSERT INTO pernoite (
                quarto_id,
                data_entrada,
                data_saida,
                quantidade_pessoa,
                status_pernoite_enum)
            VALUES (?, ?, ?, ?, 0)
            RETURNING id;
            """;

        String diaria_sql = """
            INSERT INTO diaria (
                data_inicio,
                data_fim,
                quantidade_pessoa,
                valor_diaria,
                total,
                pernoite_id,
                numero_diaria)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

        String pessoa_pernoite_sql = """
            INSERT INTO pernoite_pessoas (
                pernoite_id,
                pessoas_id)
            VALUES (?, ?);
            """;

        try (Connection connection = connect()) {

            try (PreparedStatement pernoiteStmt = connection.prepareStatement(pernoite_sql, Statement.RETURN_GENERATED_KEYS)) {
                pernoiteStmt.setLong(1, request.quarto_id());
                pernoiteStmt.setDate(2, java.sql.Date.valueOf(request.dataEntrada()));
                pernoiteStmt.setDate(3, java.sql.Date.valueOf(request.dataSaida()));
                pernoiteStmt.setInt(4, request.quantidade_de_pessoas());
                pernoiteStmt.executeUpdate();

                ResultSet generatedKeys = pernoiteStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long pernoiteId = generatedKeys.getLong(1);

                    //adiciona pessoas ao pernoite
                    try (PreparedStatement pessoaStmt = connection.prepareStatement(pessoa_pernoite_sql)) {
                        for (Long pessoaId : request.pessoas()) {
                            pessoaStmt.setLong(1, pernoiteId);
                            pessoaStmt.setLong(2, pessoaId);
                            pessoaStmt.addBatch();
                        }
                        pessoaStmt.executeBatch();
                    }

                    var quantidadeDias = Period.between(request.dataEntrada(), request.dataSaida()).getDays();
                    LocalDate diariaInicio = request.dataEntrada();

                    try (PreparedStatement diariaStmt = connection.prepareStatement(diaria_sql)) {
                        for (int i = 1; i <= quantidadeDias; i++) {

                            var valor_diaria = precosRepository.precoDiaria(request.quantidade_de_pessoas());
                            var total = request.quantidade_de_pessoas() * valor_diaria;

                            LocalDate diariaFim = diariaInicio.plusDays(1);

                            diariaStmt.setDate(1, java.sql.Date.valueOf(diariaInicio)); // data_inicio
                            diariaStmt.setDate(2, java.sql.Date.valueOf(diariaFim));    // data_fim
                            diariaStmt.setInt(3, request.quantidade_de_pessoas());      // quantidade_pessoas
                            diariaStmt.setDouble(4, valor_diaria);                      // valor diaria
                            diariaStmt.setFloat(5, total);                              // valor total
                            diariaStmt.setLong(7, pernoiteId);                          // pernoite_id
                            diariaStmt.setInt(8, i);                                    // número da diária (diária 1, 2, 3, etc.)

                            diariaStmt.addBatch(); // Adiciona ao lote

                            // Avançar a data de início para o próximo dia
                            diariaInicio = diariaFim;
                        }
                        diariaStmt.executeBatch(); // Executa o lote de inserções das diárias
                    }
                }
            } catch (SQLException e) { throw new RuntimeException(e); }
        } catch (SQLException e) { e.printStackTrace(); }
    }


}
