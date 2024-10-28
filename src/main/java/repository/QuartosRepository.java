package repository;


import config.PostgresDatabaseConnect;
import enums.StatusQuartoEnum;
import enums.TipoQuartoEnum;
import response.QuartoResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuartosRepository {
    Connection connection = PostgresDatabaseConnect.connect();

    public QuartoResponse buscaQuartoById(Long id) {
        QuartoResponse quartoResponse = null;
        String sql = "SELECT * FROM quarto WHERE id = ? limit 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    quartoResponse = new QuartoResponse(
                            resultSet.getLong("id"),
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            TipoQuartoEnum.fromCodigo(resultSet.getInt("tipo_quarto_enum")),
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return quartoResponse;
    }


    public List<QuartoResponse> buscaQuartosPorStatus(StatusQuartoEnum status) {
        List<QuartoResponse> quartos = new ArrayList<>();
        String sql = "SELECT * FROM quarto WHERE status_quarto_enum = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, status.getCodigo());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    QuartoResponse quartoResponse = new QuartoResponse(
                            resultSet.getLong("id"),
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            TipoQuartoEnum.fromCodigo(resultSet.getInt("tipo_quarto_enum")),
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum"))
                    );

                    quartos.add(quartoResponse);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return quartos;
    }

    public void alterarStatusQuarto(Long quartoId, StatusQuartoEnum status){
        String sql = """
                update quarto set status_quarto_enum = ? where id = ?
                """;
        try (PreparedStatement pessoaStatement = connection.prepareStatement(sql)) {
            pessoaStatement.setLong(1, status.getCodigo());
            pessoaStatement.setLong(2, quartoId);
            pessoaStatement.executeUpdate();
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }






}
