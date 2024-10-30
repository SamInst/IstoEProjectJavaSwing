package repository;

import config.PostgresDatabaseConnect;
import request.BuscaPessoaRequest;
import request.PessoaRequest;
import response.PessoaResponse;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PessoaRepository extends PostgresDatabaseConnect {
    Connection connection = PostgresDatabaseConnect.connect();

    public PessoaResponse buscarPessoaPorID(Long id) {
        PessoaResponse pessoa = null;
        String sql = "SELECT * FROM pessoa WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    pessoa = new PessoaResponse(
                            rs.getLong("id"),
                            rs.getTimestamp("data_hora_cadastro") != null ? rs.getTimestamp("data_hora_cadastro").toString() : null,
                            rs.getString("nome"),
                            rs.getDate("data_nascimento") != null ? rs.getDate("data_nascimento").toString() : null,
                            rs.getString("cpf"),
                            rs.getString("rg"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getString("pais"),
                            rs.getString("estado"),
                            rs.getString("municipio"),
                            rs.getString("endereco"),
                            rs.getString("complemento"),
                            rs.getBoolean("hospedado"),
                            rs.getInt("vezes_hospedado")
                    );
                }
            }

        } catch (SQLException throwables) { throwables.printStackTrace(); }

        return pessoa;
    }

    public List<BuscaPessoaRequest> buscarPessoaPorNomeCpfOuID(BuscaPessoaRequest request) {
        List<BuscaPessoaRequest> pessoas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pessoas WHERE 1=1");

        if (request.id() != null) {
            sql.append(" AND id = ?");
        }
        if (request.nome() != null && !request.nome().isBlank()) {
            sql.append(" AND nome LIKE ?");
        }
        if (request.cpf() != null && !request.cpf().isBlank()) {
            sql.append(" AND cpf = ?");
        }

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int index = 1;

            if (request.id() != null) {
                statement.setLong(index++, request.id());
            }
            if (request.nome() != null && !request.nome().isBlank()) {
                statement.setString(index++, "%" + request.nome() + "%");
            }
            if (request.cpf() != null && !request.cpf().isBlank()) {
                statement.setString(index++, request.cpf());
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {

                    BuscaPessoaRequest pessoa = new BuscaPessoaRequest(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("cpf")
                    );
                    pessoas.add(pessoa);
                }
            }

        } catch (SQLException throwables) { throwables.printStackTrace(); }
        return pessoas;
    }


    public List<BuscaPessoaRequest> buscarPessoaPorIdNomeOuCpf(String input) {

        List<BuscaPessoaRequest> pessoas = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pessoa WHERE 1=1");

        boolean isNumeric = input.matches("\\d+");

        if (isNumeric) sql.append(" AND (id = ? OR cpf = ?)");
        else sql.append(" AND nome LIKE ?");

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            if (isNumeric) {
                statement.setLong(1, Long.parseLong(input));
                statement.setString(2, input);
            } else statement.setString(1, "%" + input + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    BuscaPessoaRequest pessoa = new BuscaPessoaRequest(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("cpf")
                    );
                    pessoas.add(pessoa);
                }
            }
        } catch (SQLException throwables) { throwables.printStackTrace(); }

        return pessoas;
    }




    public void adicionarPessoa(PessoaRequest pessoaRequest) throws SQLException {
        if (cpfExists(pessoaRequest.cpf())) {
            throw new SQLException("Erro: CPF já está cadastrado no sistema.");
        }

        String sql = """
            INSERT INTO public.pessoa (
                data_hora_cadastro,
                nome,
                data_nascimento,
                cpf,
                rg,
                email,
                telefone,
                fk_pais,
                fk_estado,
                fk_municipio,
                endereco,
                complemento,
                hospedado,
                vezes_hospedado,
                cliente_novo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, pessoaRequest.nome());
            stmt.setDate(3, pessoaRequest.dataNascimento() != null ? java.sql.Date.valueOf(pessoaRequest.dataNascimento()) : null);
            stmt.setString(4, pessoaRequest.cpf());
            stmt.setString(5, pessoaRequest.rg());
            stmt.setString(6, pessoaRequest.email());
            stmt.setString(7, pessoaRequest.telefone());
            stmt.setObject(8, pessoaRequest.pais());
            stmt.setObject(9, pessoaRequest.estado());
            stmt.setObject(10, pessoaRequest.municipio());
            stmt.setString(11, pessoaRequest.endereco());
            stmt.setString(12, pessoaRequest.complemento());
            stmt.setObject(13, pessoaRequest.hospedado());
            stmt.setObject(14, pessoaRequest.vezesHospedado());
            stmt.setObject(15, pessoaRequest.clienteNovo());

            stmt.executeUpdate();
        }
    }

    private boolean cpfExists(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.pessoa WHERE cpf = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }


}
