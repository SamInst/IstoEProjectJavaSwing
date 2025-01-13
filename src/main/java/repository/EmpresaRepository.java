package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarEmpresaRequest;
import request.BuscaPessoaRequest;
import response.DadosEmpresaResponse;
import response.Objeto;

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
        String sqlEmpresa = "INSERT INTO empresa (nome_empresa, cnpj, telefone, email, endereco, cep, numero, complemento, fk_pais, fk_estado, fk_municipio, bairro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                stmtEmpresa.setString(12, empresaRequest.bairro());

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
        e.nome_empresa       AS nomeEmpresa,
        e.cnpj,
        e.telefone,
        e.email,
        e.endereco,
        e.cep,
        e.numero,
        e.complemento,
        e.bairro,
        paises.id            AS paisId,
        paises.descricao     AS paisDescricao,
        estados.id           AS estadoId,
        estados.descricao    AS estadoDescricao,
        municipios.id        AS municipioId,
        municipios.descricao AS municipioDescricao
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

                    Objeto pais = null;
                    Objeto estado = null;
                    Objeto municipio = null;

                    if (rsEmpresa.getObject("paisId") != null) {
                        pais = new Objeto(rsEmpresa.getLong("paisId"), rsEmpresa.getString("paisDescricao"));
                    }

                    if (rsEmpresa.getObject("estadoId") != null) {
                        estado = new Objeto(rsEmpresa.getLong("estadoId"), rsEmpresa.getString("estadoDescricao"));
                    }

                    if (rsEmpresa.getObject("municipioId") != null) {
                        municipio = new Objeto(rsEmpresa.getLong("municipioId"), rsEmpresa.getString("municipioDescricao"));
                    }

                    dadosEmpresa = new DadosEmpresaResponse(
                            rsEmpresa.getLong("id"),
                            rsEmpresa.getString("nomeEmpresa"),
                            rsEmpresa.getString("cnpj"),
                            rsEmpresa.getString("telefone"),
                            rsEmpresa.getString("email"),
                            rsEmpresa.getString("endereco"),
                            rsEmpresa.getString("bairro"),
                            rsEmpresa.getString("cep"),
                            rsEmpresa.getString("numero"),
                            rsEmpresa.getString("complemento"),
                            pais,
                            estado,
                            municipio,
                            pessoasVinculadas
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dadosEmpresa;
    }





    public void atualizarDadosDaEmpresa(Long empresaId, AdicionarEmpresaRequest empresaRequest) throws SQLException {
        String sql = """
                UPDATE empresa SET
                   nome_empresa = ?,
                   cnpj = ?,
                   telefone = ?,
                   email = ?,
                   endereco = ?,
                   cep = ?,
                   numero = ?,
                   complemento = ?,
                   fk_pais = ?,
                   fk_estado = ?,
                   fk_municipio = ?,
                   bairro = ?
                WHERE id = ?""";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, empresaRequest.nomeEmpresa());
            stmt.setString(2, empresaRequest.cnpj());
            stmt.setString(3, empresaRequest.telefone());
            stmt.setString(4, empresaRequest.email());
            stmt.setString(5, empresaRequest.endereco());
            stmt.setString(6, empresaRequest.cep());
            stmt.setString(7, empresaRequest.numero());
            stmt.setString(8, empresaRequest.complemento());
            stmt.setLong(9, empresaRequest.pais());
            stmt.setLong(10, empresaRequest.estado());
            stmt.setLong(11, empresaRequest.municipio());
            stmt.setString(12, empresaRequest.bairro());
            stmt.setLong(13, empresaId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Atualização falhou, nenhum registro afetado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erro ao atualizar empresa: " + e.getMessage(), e);
        }
    }

    public DadosEmpresaResponse buscarUltimaEmpresaCadastradaPorCpfPessoa(String cpf) {
        DadosEmpresaResponse dadosEmpresa = null;
        String sql = """
        SELECT
            e.id,
            e.nome_empresa       AS nomeEmpresa,
            e.cnpj,
            e.telefone,
            e.email,
            e.endereco,
            e.cep,
            e.numero,
            e.complemento,
            e.bairro,
            paises.id            AS paisId,
            paises.descricao     AS paisDescricao,
            estados.id           AS estadoId,
            estados.descricao    AS estadoDescricao,
            municipios.id        AS municipioId,
            municipios.descricao AS municipioDescricao
        FROM empresa e
        INNER JOIN empresa_pessoa ep ON e.id = ep.fk_empresa
        INNER JOIN pessoa p ON ep.fk_pessoa = p.id
        LEFT JOIN paises ON e.fk_pais = paises.id
        LEFT JOIN estados ON e.fk_estado = estados.id
        LEFT JOIN municipios ON e.fk_municipio = municipios.id
        WHERE p.cpf = ?
    order by e.id desc limit 1
    """;

        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, cpf);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {

                    List<BuscaPessoaRequest> pessoasVinculadas = pessoaRepository.buscaPessoasPorEmpresaCNPJ(rs.getString("cnpj"));

                    Objeto pais = null;
                    Objeto estado = null;
                    Objeto municipio = null;

                    if (rs.getObject("paisId") != null) {
                        pais = new Objeto(rs.getLong("paisId"), rs.getString("paisDescricao"));
                    }

                    if (rs.getObject("estadoId") != null) {
                        estado = new Objeto(rs.getLong("estadoId"), rs.getString("estadoDescricao"));
                    }

                    if (rs.getObject("municipioId") != null) {
                        municipio = new Objeto(rs.getLong("municipioId"), rs.getString("municipioDescricao"));
                    }

                    dadosEmpresa = new DadosEmpresaResponse(
                            rs.getLong("id"),
                            rs.getString("nomeEmpresa"),
                            rs.getString("cnpj"),
                            rs.getString("telefone"),
                            rs.getString("email"),
                            rs.getString("endereco"),
                            rs.getString("bairro"),
                            rs.getString("cep"),
                            rs.getString("numero"),
                            rs.getString("complemento"),
                            pais,
                            estado,
                            municipio,
                            pessoasVinculadas
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar empresa pelo CPF da pessoa: " + e.getMessage(), e);
        }

        return dadosEmpresa;
    }


}

