package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarEmpresaRequest;
import request.BuscaPessoaRequest;
import response.DadosEmpresaResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmpresaRepository {

    private final Connection conexao;
    private final PessoaRepository pessoaRepository;

    public EmpresaRepository(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
        this.conexao = PostgresDatabaseConnect.connect();
    }

    public void salvarEmpresa(AdicionarEmpresaRequest empresaRequest) throws SQLException {
        String sqlEmpresa = "INSERT INTO empresa (nome_empresa, cnpj, telefone, email, endereco, cep, numero, complemento, fk_pais, fk_estado, fk_municipio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlRelacionamento = "INSERT INTO empresa_pessoa (fk_empresa, fk_pessoa) VALUES (?, ?)";

        try {
            conexao.setAutoCommit(false);

            try (PreparedStatement stmtEmpresa = conexao.prepareStatement(sqlEmpresa, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtEmpresa.setString(1, empresaRequest.nomeEmpresa());
                stmtEmpresa.setString(2, empresaRequest.cnpj());
                stmtEmpresa.setString(3, empresaRequest.telefone());
                stmtEmpresa.setString(4, empresaRequest.email());
                stmtEmpresa.setString(5, empresaRequest.endereco());
                stmtEmpresa.setString(6, empresaRequest.cep());
                stmtEmpresa.setString(7, empresaRequest.numero());
                stmtEmpresa.setString(8, empresaRequest.complemento());
                stmtEmpresa.setLong(9, empresaRequest.pais());
                stmtEmpresa.setLong(10, empresaRequest.estado());
                stmtEmpresa.setLong(11, empresaRequest.municipio());

                int rowsAffected = stmtEmpresa.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("A inserção da empresa falhou, nenhuma linha afetada.");
                }

                long empresaId;
                try (var generatedKeys = stmtEmpresa.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empresaId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("A inserção da empresa falhou, nenhum ID gerado.");
                    }
                }

                try (PreparedStatement stmtRelacionamento = conexao.prepareStatement(sqlRelacionamento)) {
                    for (Long pessoaId : empresaRequest.pessoasVinculadas()) {
                        stmtRelacionamento.setLong(1, empresaId);
                        stmtRelacionamento.setLong(2, pessoaId);
                        stmtRelacionamento.addBatch();
                    }
                    stmtRelacionamento.executeBatch();
                }

                conexao.commit();
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao salvar empresa: " + e.getMessage(), e);
        }
    }

    public Boolean empresaCadastrada(String cnpj) {
        String sql = "SELECT COUNT(*) FROM empresa WHERE cnpj = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cnpj);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao verificar se a empresa está cadastrada: " + e.getMessage(), e);
        }
        return false;
    }

    public void vincularPessoaAEmpresa(String cnpj, Long pessoaId) throws SQLException {
        var empresa = buscarEmpresaPorCnpj(cnpj);
        String sql = """
        INSERT INTO public.empresa_pessoa (fk_empresa, fk_pessoa)
        VALUES (?, ?)
    """;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, empresa.id());
            statement.setLong(2, pessoaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erro ao vincular pessoa à empresa: " + e.getMessage());
        }
    }

    public void desvincularPessoaDaEmpresa(String cnpj, Long pessoaId) throws SQLException {
        var empresa = buscarEmpresaPorCnpj(cnpj);
        String sql = "DELETE FROM public.empresa_pessoa WHERE fk_empresa = ? AND fk_pessoa = ?";

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setLong(1, empresa.id());
            statement.setLong(2, pessoaId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erro ao desvincular pessoa da empresa: " + e.getMessage());
        }
    }


    public DadosEmpresaResponse buscarEmpresaPorCnpj(String cnpj) {
        DadosEmpresaResponse dadosEmpresa = null;
        String sqlEmpresa = """
         SELECT
        e.id,
        e.nome_empresa       nomeEmpresa,
        e.cnpj,
        e.telefone,
        e.email,
        e.endereco,
        e.cep,
        e.numero,
        e.complemento,
        paises.descricao     pais,
        estados.descricao    estado,
        municipios.descricao municipio
    
        FROM empresa e
        LEFT JOIN paises ON e.fk_pais = paises.id
        LEFT JOIN estados ON e.fk_estado = estados.id
        LEFT JOIN municipios ON e.fk_municipio = municipios.id
        WHERE e.cnpj = ?
    """;

        try (PreparedStatement statementEmpresa = conexao.prepareStatement(sqlEmpresa)) {
            statementEmpresa.setString(1, cnpj);

            try (ResultSet rsEmpresa = statementEmpresa.executeQuery()) {
                if (rsEmpresa.next()) {

                    List<BuscaPessoaRequest> pessoasVinculadas = pessoaRepository.buscaPessoasPorEmpresaCNPJ(cnpj);

                    dadosEmpresa = new DadosEmpresaResponse(
                            rsEmpresa.getLong("id"),
                            rsEmpresa.getString("nomeEmpresa"),
                            rsEmpresa.getString("cnpj"),
                            rsEmpresa.getString("telefone"),
                            rsEmpresa.getString("email"),
                            rsEmpresa.getString("endereco"),
                            rsEmpresa.getString("cep"),
                            rsEmpresa.getString("numero"),
                            rsEmpresa.getString("complemento"),
                            rsEmpresa.getString("pais"),
                            rsEmpresa.getString("estado"),
                            rsEmpresa.getString("municipio"),
                            pessoasVinculadas
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosEmpresa;
    }



}

