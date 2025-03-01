package repository;

import config.PostgresDatabaseConnect;
import response.Objeto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocalizacaoRepository {
    Connection conexao = PostgresDatabaseConnect.connect();

    public List<Objeto> buscarPaises() throws SQLException {
        String sql = "SELECT id, descricao FROM paises";
        List<Objeto> estados = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    estados.add( new Objeto(id, descricao));
                }
            }
        }
        return estados;
    }

    public List<Objeto> buscarEstadosPorPaisId(long paisId) throws SQLException {
        String sql = "SELECT id, descricao \n" +
                "FROM estados \n" +
                "WHERE fk_pais = ? \n" +
                "ORDER BY (id = 10) DESC, id;\n";
        List<Objeto> estados = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, paisId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    estados.add(new Objeto(id, descricao));
                }
            }
        }
        return estados;
    }

    public List<Objeto> buscarMunicipiosPorEstadoId(long estado_id) throws SQLException {
        String sql = "SELECT id, descricao FROM municipios WHERE fk_municipio = ?";
        List<Objeto> municipios = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, estado_id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    municipios.add( new Objeto(id, descricao));
                }
            }
        }
        return municipios;
    }


    public Objeto buscaPaisPorNome(String paisNome) throws SQLException {
        String sql = "SELECT id, descricao FROM paises WHERE descricao LIKE ?";

        Objeto paises = null;

        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, "%" + paisNome + "%");


        try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                   paises = new Objeto(id, descricao);
                }
            }
        return paises;
    }

    public Objeto buscaEstadoPorNomeEId(String estadoNome, Long pais_id) throws SQLException {
        String sql = "SELECT id, descricao FROM estados WHERE descricao LIKE ? and fk_pais = ? limit 1";

        Objeto estado = null;

        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, "%" + estadoNome + "%");
        stmt.setLong(2, pais_id);


        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String descricao = rs.getString("descricao");
                estado = new Objeto(id, descricao);
            }
        }
        return estado;
    }


    public Objeto buscaMunicipioPorNomeEId(String municipioNome, Long estado_id) throws SQLException {
        String sql = "SELECT id, descricao FROM municipios WHERE descricao like ? and fk_municipio = ?";

        Objeto municipio = null;

        PreparedStatement stmt = conexao.prepareStatement(sql);
        stmt.setString(1, municipioNome);
        stmt.setLong(2, estado_id);


        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                String descricao = rs.getString("descricao");
                municipio = new Objeto(id, descricao);
            }
        }
        return municipio;
    }

    public Objeto buscarPaisPorId(Long paisId) throws SQLException {
        String sql = "SELECT id, descricao FROM paises WHERE id = ?";
        Objeto pais = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, paisId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    pais = new Objeto(id, descricao);
                }
            }
        }
        return pais;
    }


    public Objeto buscarEstadoPorId(Long estadoId) throws SQLException {
        String sql = "SELECT id, descricao FROM estados WHERE id = ?";
        Objeto estado = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, estadoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    estado = new Objeto(id, descricao);
                }
            }
        }
        return estado;
    }

    public Objeto buscarMunicipioPorId(Long municipioId) throws SQLException {
        String sql = "SELECT id, descricao FROM municipios WHERE id = ?";
        Objeto municipio = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, municipioId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String descricao = rs.getString("descricao");
                    municipio = new Objeto(id, descricao);
                }
            }
        }
        return municipio;
    }





}
