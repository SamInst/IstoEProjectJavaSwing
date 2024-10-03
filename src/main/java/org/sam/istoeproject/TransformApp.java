package org.sam.istoeproject;

import config.PostgresDatabaseConnect;
import enums.Colors;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import principals.Reports;
import principals.Rooms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class TransformApp extends Application {

    private Parent createContent3() {
        Button button = Buttons.button("/icons/plus_icon.png", "Abrir nova tela", Colors.GREEN);
        button.setOnAction(event -> {
            Stage stage = new Stage();
            stage.show();
        });
        return new Pane(button);
    }

    private Parent createContent2() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        List<CustomerResponse> customerResponseList = new ArrayList<>();

        Button findCustomerButton = Buttons.button("/icons/plus_icon.png", "Adicionar", Colors.GREEN);

        findCustomerButton.setOnAction(event -> {
            try (Connection connection = PostgresDatabaseConnect.connect()) {
                if (connection != null) {
                    String query = """
                            select
                                id             as id,
                                nome           as nome,
                                email          as email,
                                cpf            as cpf,
                                phone          as phone,
                                datanascimento as nascimento
                            from customers;
                            """;

                    try (Statement statement = connection.createStatement()) {
                        ResultSet resultSet = statement.executeQuery(query);

                        while (resultSet.next()) {
                            Long id = resultSet.getLong("id");
                            String name = resultSet.getString("nome");
                            String email = resultSet.getString("email");
                            String cpf = resultSet.getString("cpf");
                            String phone = resultSet.getString("phone");
                            LocalDate nascimento = LocalDate.parse(resultSet.getString("nascimento"));

                            CustomerResponse customerResponse = new CustomerResponse(id, name, email, cpf, phone, nascimento);
                            customerResponseList.add(customerResponse);
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });
        return new Pane(findCustomerButton);
    }


    private Parent createContent() {
        Rectangle box = new Rectangle(100, 50, Color.BLUE);

        Button addCustomerButton = Buttons.button("/icons/plus_icon.png", "Adicionar", Colors.GREEN);

        addCustomerButton.setOnAction(event -> {
            try (Connection connection = PostgresDatabaseConnect.connect()) {

                if (connection != null) {
                    String query = """
                            insert into customers (
                            nome,
                            email,
                            cpf,
                            phone,
                            datanascimento)
                            VALUES ('sam','email','1','3', now());
                            """;

                    try (Statement statement = connection.createStatement()) {
                        statement.executeQuery(query);
                    }
                }
            } catch (Exception ignored) {}
        });

        return new Pane(
                box,
                addCustomerButton
        );
    }

    private Parent startParent() {
        Rectangle box = new Rectangle();
        return new Pane(box);
    }

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage){
        Reports.reports(new Stage());
        Rooms.start(new Stage());

    }
    }


