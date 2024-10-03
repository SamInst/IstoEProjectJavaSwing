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
                           TipoQuartoEnum.valueOf(String.valueOf(resultSet.getInt("tipo_quarto_enum"))),
                           StatusQuartoEnum.valueOf(String.valueOf(resultSet.getInt("status_quarto_enum")))
                   );
               }
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }

       System.out.println(quartoResponse);
       return quartoResponse;
   }


    public List<QuartoResponse> buscaQuartosPorStatus(StatusQuartoEnum status) {
        List<QuartoResponse> quartos = new ArrayList<>();
        String sql = "SELECT * FROM quarto WHERE status_quarto_enum = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            // Usando o código do StatusQuartoEnum
            statement.setInt(1, status.getCodigo());

            // Executando a query
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Usando o método fromCodigo de TipoQuartoEnum para mapear o tipo de quarto
                    QuartoResponse quartoResponse = new QuartoResponse(
                            resultSet.getLong("id"),
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            TipoQuartoEnum.fromCodigo(resultSet.getInt("tipo_quarto_enum")), // Corrigido aqui
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum")) // Corrigido aqui
                    );

                    // Adicionando o quarto à lista
                    quartos.add(quartoResponse);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Adicionando log para verificar se os quartos estão sendo carregados
        System.out.println("Quartos Disponíveis: " + quartos.size());

        return quartos; // Retorna a lista de quartos encontrados
    }






}
