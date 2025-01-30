package repository;

import config.PostgresDatabaseConnect;
import enums.TipoPagamentoEnum;
import request.RelatorioRequest;
import response.RelatorioRowmapper;
import response.RelatoriosResponse;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RelatoriosRepository {

    private final Connection connection = PostgresDatabaseConnect.connect();

    private RelatorioRowmapper construirRowmapper(ResultSet rs) throws SQLException {
        return new RelatorioRowmapper(
                rs.getLong("id"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("tipo_pagamento_enum"),
                rs.getString("relatorio"),
                rs.getObject("pernoite_id") != null ? rs.getLong("pernoite_id") : null,
                rs.getObject("entrada_id") != null ? rs.getLong("entrada_id") : null,
                rs.getLong("quarto_id"),
                rs.getFloat("valor")
        );
    }

    private RelatoriosResponse.Relatorios.RelatorioDoDia construirRelatorioDoDia(RelatorioRowmapper row) {
        return new RelatoriosResponse.Relatorios.RelatorioDoDia(
                row.id(),
                row.quarto_id(),
                row.data_hora().toLocalTime(),
                row.relatorio(),
                row.tipo_pagamento_enum(),
                row.valor()
        );
    }

    public List<LocalDate> datasRelatorio(int limit, int offset) {
        List<LocalDate> datas = new ArrayList<>();
        String sql = """
            SELECT DISTINCT data_hora::date AS dia
            FROM relatorio
            ORDER BY dia DESC
            LIMIT ? OFFSET ?;
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    datas.add(rs.getDate("dia").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public Float valorTotalReloatorios() {
        String sql = "SELECT SUM(valor) AS total FROM relatorio WHERE tipo_pagamento_enum = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return rs.next() ? rs.getFloat("total") : 0f;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public Float totalDoDiaRelatorios(LocalDate data) {
        String sql = """
            SELECT SUM(valor) AS totalDia
            FROM relatorio
            WHERE EXTRACT(DAY FROM data_hora) = ?
              AND EXTRACT(MONTH FROM data_hora) = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, data.getDayOfMonth());
            statement.setInt(2, data.getMonthValue());
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getFloat("totalDia") : 0f;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public List<RelatoriosResponse.Relatorios.RelatorioDoDia> relatoriosDoDia(LocalDate data) {
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> resultado = new ArrayList<>();
        String sql = """
            SELECT *
            FROM relatorio
            WHERE EXTRACT(DAY FROM data_hora) = ?
              AND EXTRACT(MONTH FROM data_hora) = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, data.getDayOfMonth());
            statement.setInt(2, data.getMonthValue());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var row = construirRowmapper(rs);
                    resultado.add(construirRelatorioDoDia(row));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public RelatoriosResponse relatoriosResponse() {
        var datas = datasRelatorio(4, 0);
        var relatorios = new ArrayList<RelatoriosResponse.Relatorios>();
        datas.forEach(data -> {
            var formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            relatorios.add(new RelatoriosResponse.Relatorios(
                    data.format(formato),
                    totalDoDiaRelatorios(data),
                    relatoriosDoDia(data)
            ));
        });
        var listaOrdenada = relatorios.stream()
                .map(r -> new RelatoriosResponse.Relatorios(
                        r.data(),
                        r.total_do_dia(),
                        r.relatorioDoDia().stream()
                                .sorted(Comparator.comparing(RelatoriosResponse.Relatorios.RelatorioDoDia::horario)
                                        .reversed())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new RelatoriosResponse(valorTotalReloatorios(), listaOrdenada);
    }

    public RelatoriosResponse.Relatorios buscaRelatorioPorData(LocalDate data) {
        var formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new RelatoriosResponse.Relatorios(
                data.format(formato),
                totalDoDiaRelatorios(data),
                relatoriosDoDia(data)
        );
    }

    public List<RelatoriosResponse.Relatorios.RelatorioDoDia> buscaPorDataETipo(LocalDate data, TipoPagamentoEnum tipo) {
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> resultado = new ArrayList<>();
        String sql = """
            SELECT *
            FROM relatorio
            WHERE EXTRACT(DAY FROM data_hora) = ?
              AND EXTRACT(MONTH FROM data_hora) = ?
              AND tipo_pagamento_enum = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, data.getDayOfMonth());
            statement.setInt(2, data.getMonthValue());
            statement.setInt(3, tipo.getCodigo());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var row = construirRowmapper(rs);
                    resultado.add(construirRelatorioDoDia(row));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public List<RelatoriosResponse.Relatorios.RelatorioDoDia> buscaPorTipo(TipoPagamentoEnum tipo) {
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> resultado = new ArrayList<>();
        String sql = """
            SELECT *
            FROM relatorio
            WHERE tipo_pagamento_enum = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tipo.getCodigo());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var row = construirRowmapper(rs);
                    resultado.add(construirRelatorioDoDia(row));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public List<LocalDate> datasPorTipo(TipoPagamentoEnum tipo) {
        List<LocalDate> datas = new ArrayList<>();
        String sql = """
            SELECT DISTINCT CAST(data_hora AS date) AS dia
            FROM relatorio
            WHERE tipo_pagamento_enum = ?
            ORDER BY dia DESC
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tipo.getCodigo());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    datas.add(rs.getDate("dia").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public List<LocalDate> datasRetirada() {
        List<LocalDate> datas = new ArrayList<>();
        String sql = """
            SELECT DISTINCT CAST(data_hora AS date) AS dia
            FROM relatorio
            WHERE tipo_pagamento_enum = 1
              AND valor < 0
            ORDER BY dia DESC
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                datas.add(rs.getDate("dia").toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datas;
    }

    public List<RelatoriosResponse.Relatorios.RelatorioDoDia> buscaRetiradaPorData(LocalDate date) {
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> resultado = new ArrayList<>();
        String sql = """
            SELECT *
            FROM relatorio
            WHERE tipo_pagamento_enum = 1
              AND valor < 0
              AND CAST(data_hora AS date) = ?
        """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    var row = construirRowmapper(rs);
                    resultado.add(construirRelatorioDoDia(row));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public void adicionarRelatorio(RelatorioRequest request) {
        String sql = """
            INSERT INTO relatorio (
               data_hora,
               tipo_pagamento_enum,
               relatorio,
               valor,
               quarto_id
            )
            VALUES (?,?,?,?,?)
        """;
        if (request.quarto_id() != null) {
            QuartosRepository quartosRepository = new QuartosRepository();
            quartosRepository.buscaQuartoPorId(request.quarto_id());
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, request.tipo_pagamento_enum().getCodigo());
            statement.setString(3, request.relatorio());
            statement.setFloat(4, request.valor());
            if (request.quarto_id() == null) {
                statement.setNull(5, Types.BIGINT);
            } else {
                statement.setLong(5, request.quarto_id());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public float totalPorTipo(TipoPagamentoEnum tipo) {
        float total = 0f;
        String sql = """
        SELECT COALESCE(SUM(valor), 0)
        FROM relatorio
        WHERE tipo_pagamento_enum = ?
    """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tipo.getCodigo());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public float totalNegativo() {
        float total = 0f;
        String sql = """
        SELECT COALESCE(SUM(valor), 0)
        FROM relatorio
        WHERE tipo_pagamento_enum = ?
          AND valor < 0
    """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, TipoPagamentoEnum.DINHEIRO.getCodigo());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public float totalPorTipoEData(LocalDate data, TipoPagamentoEnum tipo) {
        float total = 0f;
        String sql = """
        SELECT COALESCE(SUM(valor), 0)
        FROM relatorio
        WHERE tipo_pagamento_enum = ?
          AND CAST(data_hora AS date) = ?
    """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tipo.getCodigo());
            statement.setDate(2, java.sql.Date.valueOf(data));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public Float somaValorRelatorioMaisAnteriores(long id) {
        String sql = """
        SELECT id, valor, tipo_pagamento_enum,
            SUM(CASE WHEN tipo_pagamento_enum = '1' THEN valor ELSE 0 END) OVER (ORDER BY id ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS somaTotal
        FROM relatorio
        WHERE id <= ?
        ORDER BY id
    """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                float previousSomaTotal = 0;

                while (rs.next()) {
                    float valor = rs.getFloat("valor");
                    String tipoPagamento = rs.getString("tipo_pagamento_enum");
                    float somaTotal = rs.getFloat("somaTotal");

                    if (rs.getLong("id") == id) {
                        return "1".equals(tipoPagamento) ? somaTotal : previousSomaTotal;
                    }

                    previousSomaTotal = somaTotal;
                }
                return 0f;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return 0f;
        }
    }







}
