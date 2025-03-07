package repository;

import config.PostgresDatabaseConnect;
import enums.StatusQuartoEnum;
import request.AtualizarDadosQuartoRequest;
import response.Objeto;
import response.QuartoResponse;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuartosRepository {
    Connection connection = PostgresDatabaseConnect.connect();

    String quarto_sql = """
    SELECT distinct q.id AS quarto_id,
    q.descricao,
    q.quantidade_pessoas,
    q.qtd_cama_casal,
    q.qtd_cama_solteiro,
    q.qtd_beliche,
    q.qtd_rede,
    q.status_quarto_enum,
    c.id AS categoria_id,
    c.categoria
    FROM quarto q
    LEFT JOIN categoria c ON q.fk_categoria = c.id
    """;


    public Objeto buscaCategoriaPorNome(String categoria){
        String sql = """
                select categoria.id, categoria.categoria from categoria where categoria.categoria = ?;
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                   return new Objeto(
                            resultSet.getLong("id"),
                            resultSet.getString("categoria")
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Categoria de quarto nao encontrada " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public List<QuartoResponse> buscaQuartosPorStatus(StatusQuartoEnum status) {
        List<QuartoResponse> quartos = new ArrayList<>();
        String sql = quarto_sql + " WHERE q.status_quarto_enum = ? ";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status.getCodigo());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long quartoId = resultSet.getLong("quarto_id");
                    QuartoResponse.Categoria categoria = new QuartoResponse.Categoria(
                            resultSet.getLong("categoria_id"),
                            resultSet.getString("categoria"),
                            getValorPessoaList(resultSet.getLong("categoria_id"))
                    );

                    QuartoResponse quarto = new QuartoResponse(
                            quartoId,
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum")),
                            resultSet.getInt("qtd_cama_casal"),
                            resultSet.getInt("qtd_cama_solteiro"),
                            resultSet.getInt("qtd_beliche"),
                            resultSet.getInt("qtd_rede"),
                            categoria
                    );
                    quartos.add(quarto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return quartos;
    }


    public List<Objeto> listarCategorias() {
        List<Objeto> categorias = new ArrayList<>();
        String sql = "select id, categoria.categoria from categoria; ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Objeto categoria = new Objeto(
                            resultSet.getLong("id"),
                            resultSet.getString("categoria")
                    );
                    categorias.add(categoria);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categorias;
    }

    public List<QuartoResponse> buscaTodosOsQuartos() {
        List<QuartoResponse> quartos = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(quarto_sql.toString())) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Long quartoId = resultSet.getLong("quarto_id");
                    QuartoResponse.Categoria categoria = new QuartoResponse.Categoria(
                            resultSet.getLong("categoria_id"),
                            resultSet.getString("categoria"),
                            getValorPessoaList(resultSet.getLong("categoria_id"))
                    );

                    QuartoResponse quarto = new QuartoResponse(
                            quartoId,
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum")),
                            resultSet.getInt("qtd_cama_casal"),
                            resultSet.getInt("qtd_cama_solteiro"),
                            resultSet.getInt("qtd_beliche"),
                            resultSet.getInt("qtd_rede"),
                            categoria
                    );
                    quartos.add(quarto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return quartos;
    }

    private List<QuartoResponse.Categoria.ValorPessoa> getValorPessoaList(Long categoriaId) {
        List<QuartoResponse.Categoria.ValorPessoa> valorPessoaList = new ArrayList<>();
        String sql = "SELECT qtd_pessoa, valor FROM preco_pessoa_categoria WHERE fk_categoria = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, categoriaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    QuartoResponse.Categoria.ValorPessoa valorPessoa = new QuartoResponse.Categoria.ValorPessoa(
                            resultSet.getInt("qtd_pessoa"),
                            resultSet.getFloat("valor")
                    );
                    valorPessoaList.add(valorPessoa);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return valorPessoaList;
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

    public void atualizarDadosQuarto(AtualizarDadosQuartoRequest request) {
        String sql = """
            UPDATE quarto
            SET descricao = ?,
                quantidade_pessoas = ?,
                qtd_cama_casal = ?,
                qtd_cama_solteiro = ?,
                qtd_beliche = ?,
                qtd_rede = ?,
                fk_categoria = ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.descricao());
            statement.setInt(2, request.quantidadePessoas());
            statement.setInt(3, request.qtdCamaCasal());
            statement.setInt(4, request.qtdCamaSolteiro());
            statement.setInt(5, request.qtdCamaBeliche());
            statement.setInt(6, request.qtdRede());
            statement.setLong(7, request.categoriaId());
            statement.setLong(9, request.id());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Erro: Quarto com ID " + request.id() + " não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar os dados do quarto: " + e.getMessage(), e);
        }
    }

    public QuartoResponse buscaQuartoPorId(Long id) {
        String sql = quarto_sql + " WHERE q.id = ?";

        QuartoResponse quartoResponse = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long categoriaId = resultSet.getLong("categoria_id");
                    String categoriaNome = resultSet.getString("categoria");

                    List<QuartoResponse.Categoria.ValorPessoa> valorPessoaList = getValorPessoaList(categoriaId);

                    QuartoResponse.Categoria categoria = new QuartoResponse.Categoria(categoriaId, categoriaNome, valorPessoaList);
                    quartoResponse = new QuartoResponse(
                            resultSet.getLong("quarto_id"),
                            resultSet.getString("descricao"),
                            resultSet.getInt("quantidade_pessoas"),
                            StatusQuartoEnum.fromCodigo(resultSet.getInt("status_quarto_enum")),
                            resultSet.getInt("qtd_cama_casal"),
                            resultSet.getInt("qtd_cama_solteiro"),
                            resultSet.getInt("qtd_beliche"),
                            resultSet.getInt("qtd_rede"),
                            categoria
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar quarto por ID: " + e.getMessage(), e);
        }

        return quartoResponse;
    }


    public void salvarQuarto(AtualizarDadosQuartoRequest request) {
        String sql = """
        INSERT INTO quarto (
            descricao,
            quantidade_pessoas,
            qtd_cama_casal,
            qtd_cama_solteiro,
            qtd_beliche,
            qtd_rede,
            fk_categoria,
            status_quarto_enum
        ) VALUES (?, ?, ?, ?, ?, ?, ?, 2)
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.descricao());
            statement.setInt(2, request.quantidadePessoas());
            statement.setInt(3, request.qtdCamaCasal());
            statement.setInt(4, request.qtdCamaSolteiro());
            statement.setInt(5, request.qtdCamaBeliche());
            statement.setInt(6, request.qtdRede());
            statement.setLong(7, request.categoriaId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Erro ao salvar o quarto: Nenhuma linha foi inserida.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o quarto: " + e.getMessage(), e);
        }
    }

    public boolean verificaQuartoExistente(Long numeroQuarto) {
        String sql = "SELECT COUNT(*) AS total FROM quarto WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, numeroQuarto);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar a existência do quarto: " + e.getMessage(), e);
        }
        return false;
    }

    public Float getValorCategoria(Long quartoId, int quantidadePessoas) {
        String sql = """
        SELECT p.valor
        FROM quarto q
        JOIN preco_pessoa_categoria p ON q.fk_categoria = p.fk_categoria
        WHERE q.id = ? AND p.qtd_pessoa = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, quartoId);
            stmt.setInt(2, quantidadePessoas);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("valor");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar valor da categoria: " + e.getMessage(), e);
        }
    }



}
