package repository;

import config.PostgresDatabaseConnect;
import tools.Resize;
import request.BuscaPessoaRequest;
import request.PessoaRequest;
import response.Objeto;
import response.PessoaResponse;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PessoaRepository extends PostgresDatabaseConnect {
    Connection connection = PostgresDatabaseConnect.connect();

    String sql = """
        SELECT  p.id,
                data_hora_cadastro,
                nome,
                data_nascimento,
                idade,
                cep,
                numero,
                bairro,
                cpf,
                rg,
                email,
                telefone,
                pa.id pais_id,
                pa.descricao pais,
                e.id estado_id,
                e.descricao estado,
                m.id municipio_id,
                m.descricao municipio,
                endereco,
                complemento,
                hospedado,
                cliente_novo,
                vezes_hospedado,
                sexo
           FROM pessoa p
           JOIN public.estados e ON e.id = p.fk_estado
           JOIN public.municipios m ON m.id = p.fk_municipio
           JOIN public.paises pa ON pa.id = p.fk_pais
        """;

    public PessoaResponse buscarPessoaPorID(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(sql + " WHERE p.id = ?")) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return pessoaResponse(rs);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }


    public PessoaResponse buscarPessoaPorCPF(String cpf) {
        try (PreparedStatement statement = connection.prepareStatement(sql + " WHERE p.cpf = ?")) {
            statement.setString(1, cpf);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return pessoaResponse(rs);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return null;
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




    public Long adicionarPessoa(PessoaRequest pessoaRequest) throws SQLException {
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
            cliente_novo,
            sexo,
            idade,
            bairro,
            cep,
            numero
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id;
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
            stmt.setObject(16, pessoaRequest.sexo());
            stmt.setObject(17, pessoaRequest.idade());
            stmt.setObject(18, pessoaRequest.bairro());
            stmt.setObject(19, pessoaRequest.cep());
            stmt.setObject(20, pessoaRequest.numero());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                else throw new SQLException("Erro ao inserir pessoa: Nenhum ID retornado.");
            }
        }
    }


    public boolean cpfExists(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM public.pessoa WHERE cpf = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<BuscaPessoaRequest> buscaPessoasPorEmpresaCNPJ(String cnpj) {
        List<BuscaPessoaRequest> pessoas = new ArrayList<>();
        if (cnpj != null){
        String sql = """
        SELECT distinct p.id, p.nome, p.cpf
        FROM empresa_pessoa ep
        JOIN empresa e ON ep.fk_empresa = e.id
        join public.pessoa p on p.id = ep.fk_pessoa
        WHERE e.cnpj = ?
        order by p.nome
        """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, cnpj);

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
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return pessoas;
    }

    public void adicionarFotoPessoa(Long pessoaID, String path) throws SQLException {
        String sql = "insert into foto_pessoa (fk_pessoa, path) VALUES (?,?);";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pessoaID);
            stmt.setString(2, path);
            stmt.executeUpdate();
        }
    }

    public ImageIcon buscarFotoPessoaPorId(Long pessoaID) throws SQLException {
        String sql = "SELECT path FROM foto_pessoa WHERE fk_pessoa = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pessoaID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String path = rs.getString("path");
                    File fotoArquivo = new File(path);

                    if (fotoArquivo.exists()) {
                        ImageIcon imageIcon = new ImageIcon(path);
                        return Resize.resizeIcon(imageIcon, 180, 180);
                    }
                }
               return null;
            }
        }
    }

    public BufferedImage buscarFotoBufferedPessoaPorId(Long pessoaID) throws SQLException, IOException {
        String sql = "SELECT path FROM foto_pessoa WHERE fk_pessoa = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pessoaID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String path = rs.getString("path");
                    File fotoArquivo = new File(path);

                    if (fotoArquivo.exists()) {

                        return ImageIO.read(fotoArquivo);
                    }
                }
                return null;
            }
        }
    }

    public Objeto buscarPathFotoPessoaPorId(Long pessoaID) throws SQLException {
        String sql = "SELECT id, path FROM foto_pessoa WHERE fk_pessoa = ?;";

        var pessoa = buscarPessoaPorID(pessoaID);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pessoaID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new Objeto(
                        rs.getLong("id"),
                        rs.getString("path")
                );
            }
            return null;
        }
    }

    public void atualizarPathFotoPessoaPorFotoId(Long fotoID, String novoPath) throws SQLException {
        String sql = "UPDATE foto_pessoa SET path = ? WHERE id = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoPath);
            stmt.setLong(2, fotoID);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Nenhuma foto encontrada para a pessoa com ID: " + fotoID);
            }
        }
    }



    public void atualizarPessoa(Long pessoaID, PessoaRequest pessoaRequest) throws SQLException {
        String sql = """
        UPDATE public.pessoa
        SET nome = ?,
            data_nascimento = ?,
            cpf = ?,
            rg = ?,
            email = ?,
            telefone = ?,
            fk_pais = ?,
            fk_estado = ?,
            fk_municipio = ?,
            endereco = ?,
            complemento = ?,
            hospedado = ?,
            vezes_hospedado = ?,
            cliente_novo = ?,
            sexo = ?,
            idade = ?,
            bairro = ?,
            cep = ?,
            numero = ?
        WHERE id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pessoaRequest.nome());
            stmt.setDate(2, pessoaRequest.dataNascimento() != null ? Date.valueOf(pessoaRequest.dataNascimento()) : null);
            stmt.setString(3, pessoaRequest.cpf());
            stmt.setString(4, pessoaRequest.rg());
            stmt.setString(5, pessoaRequest.email());
            stmt.setString(6, pessoaRequest.telefone());
            stmt.setObject(7, pessoaRequest.pais());
            stmt.setObject(8, pessoaRequest.estado());
            stmt.setObject(9, pessoaRequest.municipio());
            stmt.setString(10, pessoaRequest.endereco());
            stmt.setString(11, pessoaRequest.complemento());
            stmt.setObject(12, pessoaRequest.hospedado());
            stmt.setObject(13, pessoaRequest.vezesHospedado());
            stmt.setObject(14, pessoaRequest.clienteNovo());
            stmt.setObject(15, pessoaRequest.sexo());
            stmt.setObject(16, pessoaRequest.idade());
            stmt.setObject(17, pessoaRequest.bairro());
            stmt.setObject(18, pessoaRequest.cep());
            stmt.setObject(19, pessoaRequest.numero());
            stmt.setLong(20, pessoaID);

            int rowsUpdated = stmt.executeUpdate();
        }
    }

    public List<PessoaResponse> buscarTodasAsPessoasComPaginacao(int page) {
        List<PessoaResponse> pessoas = new ArrayList<>();
        int size = qutPessoasHospedadas() + 3;
        int offset = page * size;

        try (PreparedStatement statement = connection.prepareStatement(
                sql + """
                 ORDER BY CASE WHEN p.hospedado = true THEN 0 ELSE 1 END,
                 p.nome 
                 LIMIT ? OFFSET ?""")) {
            statement.setInt(1, size);
            statement.setInt(2, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pessoas.add(pessoaResponse(rs));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return pessoas;
    }

    private PessoaResponse pessoaResponse(ResultSet rs) throws SQLException {
        return new PessoaResponse(
                rs.getLong("id"),
                rs.getTimestamp("data_hora_cadastro") != null ? rs.getTimestamp("data_hora_cadastro").toString() : null,
                rs.getString("nome"),
                rs.getDate("data_nascimento") != null ? rs.getDate("data_nascimento").toLocalDate() : null,
                rs.getString("cpf"),
                rs.getString("rg"),
                rs.getString("email"),
                rs.getString("telefone"),
                new Objeto(rs.getLong("pais_id"), rs.getString("pais")),
                new Objeto(rs.getLong("estado_id"), rs.getString("estado")),
                new Objeto(rs.getLong("municipio_id"), rs.getString("municipio")),
                rs.getString("endereco"),
                rs.getString("complemento"),
                rs.getBoolean("hospedado"),
                rs.getBoolean("cliente_novo"),
                rs.getInt("vezes_hospedado"),
                rs.getString("cep"),
                rs.getString("bairro"),
                rs.getInt("idade"),
                rs.getString("numero"),
                rs.getInt("sexo")
        );
    }

    public Integer qutPessoasHospedadas() {
        String sql = "SELECT COUNT(*) FROM pessoa WHERE hospedado = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar quantidade de pessoas hospedadas: " + e.getMessage(), e);
        }

        return 0;
    }

    public List<PessoaResponse> buscarPessoaPorNome(String nome) {
        List<PessoaResponse> pessoas = new ArrayList<>();
        String sql_busca_nome = sql + " WHERE p.nome ILIKE ? ORDER BY p.nome";

        try (PreparedStatement statement = connection.prepareStatement(sql_busca_nome);) {
            statement.setString(1, "%" + nome + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pessoas.add(pessoaResponse(rs));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return pessoas;
    }

    public List<PessoaResponse> buscarPessoasHospedadas() {
        List<PessoaResponse> pessoas = new ArrayList<>();
        String sql_busca_hospedados = sql + " WHERE p.hospedado = true ORDER BY p.nome";

        try (PreparedStatement statement = connection.prepareStatement(sql_busca_hospedados);) {

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pessoas.add(pessoaResponse(rs));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return pessoas;
    }


}