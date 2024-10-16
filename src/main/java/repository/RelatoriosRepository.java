package repository;

import config.PostgresDatabaseConnect;
import org.springframework.jdbc.core.RowMapper;
import request.RelatorioRequest;
import response.RelatorioRowmapper;
import response.RelatoriosResponse;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RelatoriosRepository {
    Connection connection = PostgresDatabaseConnect.connect();

    public List<LocalDate> datasRelatorio(int limit, int offset) {
        List<LocalDate> datas = new ArrayList<>();

        String sql = """
        SELECT data_hora::date
        FROM relatorio
        order by data_hora::date desc
        LIMIT ? OFFSET ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    datas.add(rs.getDate("data_hora").toLocalDate());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return datas;
    }


    public Float valorTotalReloatorios() {
        Float total = 0f;
        String sql = "select sum(valor) from relatorio where tipo_pagamento_enum = 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) total = rs.getFloat("sum");
            }
        } catch (SQLException throwables) { throwables.printStackTrace(); }
        return total;
    }

    public Float totalDoDiaRelatorios(LocalDate data) {
        float total = 0f;
        String sql = """
        select sum(valor) as totalDia
        from relatorio
        WHERE EXTRACT(DAY FROM data_hora) = ?
        AND EXTRACT(MONTH FROM data_hora) = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, data.getDayOfMonth());
            statement.setInt(2, data.getMonthValue());

            try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) total = rs.getFloat("totalDia");
            }
        } catch (SQLException throwables) { throwables.printStackTrace(); }
        return total;
    }


    public RelatoriosResponse relatoriosResponse () {
        List<RelatoriosResponse.Relatorios> relatorios = new ArrayList<>();

        datasRelatorio(4, 0).forEach(data -> {
            RelatoriosResponse.Relatorios relatorio = new RelatoriosResponse.Relatorios(
                    data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    totalDoDiaRelatorios(data),
                    relatoriosDoDia(data)
            );
            relatorios.add(relatorio);
        });

        return new RelatoriosResponse(
                valorTotalReloatorios(),
                relatorios.stream()
                        .map(relatorio -> new RelatoriosResponse.Relatorios(
                                relatorio.data(),
                                relatorio.total_do_dia(),
                                relatorio.relatorioDoDia().stream()
                                        .sorted(Comparator.comparing(RelatoriosResponse.Relatorios.RelatorioDoDia::horario)
                                        .reversed())
                                        .toList()
                        ))
                        .toList()
        );
    }

    public RelatoriosResponse.Relatorios buscaRelatorioPorData(LocalDate data) {
        return new RelatoriosResponse.Relatorios(
                data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                totalDoDiaRelatorios(data),
                relatoriosDoDia(data)
        );
    }



    public List<RelatoriosResponse.Relatorios.RelatorioDoDia> relatoriosDoDia(LocalDate data) {
        List<RelatoriosResponse.Relatorios.RelatorioDoDia> relatoriosDoDia = new ArrayList<>();

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

                    RelatorioRowmapper relatorioRowmapper = new RelatorioRowmapper(
                            rs.getLong("id"),
                            rs.getTimestamp("data_hora").toLocalDateTime(),
                            rs.getString("tipo_pagamento_enum"),
                            rs.getString("relatorio"),
                            rs.getObject("pernoite_id") != null ? rs.getLong("pernoite_id") : null,
                            rs.getObject("entrada_id") != null ? rs.getLong("entrada_id") : null,
                            rs.getFloat("valor")
                    );

                    RelatoriosResponse.Relatorios.RelatorioDoDia relatorioDoDia = new RelatoriosResponse.Relatorios.RelatorioDoDia(
                            relatorioRowmapper.id(),
                            relatorioRowmapper.data_hora().toLocalTime(),
                            relatorioRowmapper.relatorio(),
                            relatorioRowmapper.tipo_pagamento_enum(),
                            relatorioRowmapper.valor()
                    );
                    relatoriosDoDia.add(relatorioDoDia);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return relatoriosDoDia;
    }

    public void adicionarRelatorio(RelatorioRequest request){
        Long pernoite_id;
        Long entrada_id;

        String sql = """
        insert into relatorio (
           data_hora,
           tipo_pagamento_enum,
           relatorio,
           pernoite_id,
           entrada_id,
           valor,
           quarto_id)
        VALUES (?,?,?,?,?,?,?)
        """;

        if (request.quarto_id() != null){
            QuartosRepository quartosRepository = new QuartosRepository();
            var quarto = quartosRepository.buscaQuartoById(request.quarto_id());
            System.out.println(quarto.descricao());
        }

        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, timestamp);
            statement.setInt(2, request.tipo_pagamento_enum().getCodigo());
            statement.setString(3, request.relatorio());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }





}
