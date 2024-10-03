package repository;

import config.PostgresDatabaseConnect;
import response.PrecoPernoiteResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrecosRepository extends PostgresDatabaseConnect {

    public Float precoDiaria(int quantidadePessoas) {
        String sql = """
        SELECT valor_diaria
        FROM preco_pernoite
        WHERE quant_pessoa = ?;
        """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantidadePessoas);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return resultSet.getFloat("valor_diaria");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<PrecoPernoiteResponse> valorDiaria() {
        String sql = """
            SELECT quant_pessoa, valor_diaria
            FROM preco_pernoite;
            """;

        List<PrecoPernoiteResponse> precos = new ArrayList<>();

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer quantPessoas = resultSet.getInt("quant_pessoa");
                Float valorDiaria = resultSet.getFloat("valor_diaria");

                PrecoPernoiteResponse precoPernoite = new PrecoPernoiteResponse(quantPessoas, valorDiaria);
                precos.add(precoPernoite);
            }

        } catch (SQLException e) { e.printStackTrace(); }

        return precos;
    }
}
