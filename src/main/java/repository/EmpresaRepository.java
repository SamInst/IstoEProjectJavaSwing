package repository;

import config.PostgresDatabaseConnect;
import request.AdicionarEmpresaRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmpresaRepository {

    private final Connection conexao;

    public EmpresaRepository() {
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
}

