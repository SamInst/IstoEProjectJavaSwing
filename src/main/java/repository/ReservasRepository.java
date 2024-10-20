package repository;

import config.PostgresDatabaseConnect;
import org.springframework.transaction.annotation.Transactional;
import request.AdicionarReservasRequest;
import request.BuscaReservasResponse;
import response.DiariaResponse;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservasRepository {
    Connection connection = PostgresDatabaseConnect.connect();

    @Transactional
    public void adicionarReserva(AdicionarReservasRequest request) {
        String sql_reserva = """
            insert into reservas (quarto_id, data_entrada, data_saida, quantidade_pessoas, ativa) VALUES (?,?,?,?, true) returning reserva_id;
            """;

        String sql_reserva_pessoas = """
            insert into reserva_pessoas (reserva_id, pessoa_id) VALUES (?,?);
            """;

        String sql_reserva_pagamentos = """
            insert into reserva_pagamento (reserva_id, valor, data_hora_pagamento, tipo_pagamento) VALUES (?,?,?,?);
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql_reserva)) {
            statement.setLong(1, request.quarto());
            statement.setDate(2, Date.valueOf(request.data_entrada()));
            statement.setDate(3, Date.valueOf(request.data_saida()));
            statement.setInt(4, request.quantidade_pessoas());

            ResultSet generatedKeys = statement.executeQuery();

            if (generatedKeys.next()) {
                long reserva_id = generatedKeys.getLong(1);

                if (request.pessoas() != null){
                    request.pessoas().forEach(pessoa_id -> {
                        try (PreparedStatement pessoaStatement = connection.prepareStatement(sql_reserva_pessoas)) {
                            pessoaStatement.setLong(1, reserva_id);
                            pessoaStatement.setLong(2, pessoa_id);
                            pessoaStatement.executeUpdate();
                        } catch (SQLException throwables) { throwables.printStackTrace(); }
                    });
                }

                if (request.pagamentos() != null){
                    request.pagamentos().forEach(pagamento -> {
                        try (PreparedStatement pagamentoStatement = connection.prepareStatement(sql_reserva_pagamentos)) {
                            pagamentoStatement.setLong(1, reserva_id);
                            pagamentoStatement.setFloat(2, pagamento.valor_pagamento());
                            pagamentoStatement.setDate(3, Date.valueOf(LocalDate.now()));
                            pagamentoStatement.setString(4, pagamento.tipo_pagamento());
                            pagamentoStatement.executeUpdate();
                        } catch (SQLException throwables) { throwables.printStackTrace(); }
                    });
                }
            }
        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }


    public List<LocalDate> datasReservadas(){
        List<LocalDate> listaDatasReservadas = new ArrayList<>();

        String sql = """
                select distinct r.data_entrada
                from reservas r where data_entrada >= now()
                and r.ativa = true
                order by data_entrada;
                """;

        try (PreparedStatement diariaStmt = connection.prepareStatement(sql)) {
            ResultSet rsDiaria = diariaStmt.executeQuery();

            while (rsDiaria.next()) {
                listaDatasReservadas.add(rsDiaria.getDate("data_entrada").toLocalDate());
            }

        } catch (Exception e){ e.printStackTrace(); }
        return listaDatasReservadas;
    }



    public List<BuscaReservasResponse> todasReservas(){
        List<BuscaReservasResponse> listaBuscaReservas = new ArrayList<>();

        datasReservadas().forEach(dataReservada -> {
            listaBuscaReservas.add(buscaReservasPorData(dataReservada));
        });
        return listaBuscaReservas;
    }




    public BuscaReservasResponse buscaReservasPorData(LocalDate data){

        List<BuscaReservasResponse.Reservas> listaReservas = new ArrayList<>();

        String sql_reservas = """
            select *
            from reservas r where ? between r.data_entrada and r.data_saida
            and r.ativa = true
            order by r.data_entrada;
            """;

        String sql_pessoas = """
            select
                p.id       as pessoa_id,
                p.nome     as nome,
                p.telefone as telefone
            from reserva_pessoas
            left join pessoa p on p.id = reserva_pessoas.pessoa_id
            where reserva_id = ?;
            """;

        String sql_pagamentos = """
            select
                valor,
                data_hora_pagamento,
                tipo_pagamento
            from reserva_pagamento where reserva_id = ?;
            """;

        try (PreparedStatement reservaStmt = connection.prepareStatement(sql_reservas)) {
            reservaStmt.setDate(1, Date.valueOf(data));
            ResultSet rsReserva = reservaStmt.executeQuery();

            while (rsReserva.next()) {
                long reservaId = rsReserva.getLong("reserva_id");

                List<BuscaReservasResponse.Reservas.Pessoas> listaPessoas = new ArrayList<>();
                try (PreparedStatement pessoasStmt = connection.prepareStatement(sql_pessoas)) {
                    pessoasStmt.setLong(1, reservaId);
                    ResultSet rsPessoas = pessoasStmt.executeQuery();

                    while (rsPessoas.next()) {
                        listaPessoas.add(new BuscaReservasResponse.Reservas.Pessoas(
                                rsPessoas.getLong("pessoa_id"),
                                rsPessoas.getString("nome"),
                                rsPessoas.getString("telefone")
                        ));
                    }
                }

                List<BuscaReservasResponse.Reservas.Pagamentos> pagamentos = new ArrayList<>();
                try (PreparedStatement pagamentoStmt = connection.prepareStatement(sql_pagamentos)) {
                    pagamentoStmt.setLong(1, reservaId);
                    ResultSet rsPagamentos = pagamentoStmt.executeQuery();

                    if (rsPagamentos.next()) {
                        pagamentos.add(new BuscaReservasResponse.Reservas.Pagamentos(
                                rsPagamentos.getString("tipo_pagamento"),
                                rsPagamentos.getFloat("valor"),
                                rsPagamentos.getString("data_hora_pagamento")
                        ));
                    }
                }

                listaReservas.add(new BuscaReservasResponse.Reservas(
                        reservaId,
                        rsReserva.getLong("quarto_id"),
                        rsReserva.getDate("data_entrada").toLocalDate(),
                        rsReserva.getDate("data_saida").toLocalDate(),
                        listaPessoas,
                        pagamentos
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BuscaReservasResponse(data, listaReservas);
    }



}
