package org.sam.istoeproject.modals;

import config.PostgresDatabaseConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonIdentificationForm {

    public static void personIdentification(Stage primaryStage) {
        primaryStage.setTitle("Identificação de Pessoa");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #CCCCCC; -fx-border-radius: 15; -fx-background-radius: 15;");

        Label sectionLabel = new Label("Identificação de Pessoa");
        sectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 0, 0, 0));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField typeField = new TextField();
        typeField.setPromptText("Pessoa Física");

        TextField cpfField = new TextField();
        cpfField.setPromptText("050.432.263-07");

        TextField rgField = new TextField();
        rgField.setPromptText("050432263-07");

        TextField nameField = new TextField();
        nameField.setPromptText("Sam Helson Nunes Diniz");

        TextField phoneField = new TextField();
        phoneField.setPromptText("(98) 98450-8897");

        TextField emailField = new TextField();
        emailField.setPromptText("sam04hel@gmail.com");

        // DatePicker para selecionar a data de nascimento
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Selecione a data de nascimento");

        // Formatar a exibição do DatePicker para dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                } else {
                    return null;
                }
            }
        });

        // Restringir datas futuras
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        TextField addressField = new TextField();
        addressField.setPromptText("Rua do abacaxi sem coroa");

        TextField numberField = new TextField();
        numberField.setPromptText("300");

        TextField complementField = new TextField();
        complementField.setPromptText("Proximo ao estadio assomblado de viana");

        TextField countryField = new TextField("Brasil");
        countryField.setPromptText("Brasil");

        TextField stateField = new TextField("Maranhão");
        stateField.setPromptText("Maranhão");

        TextField cityField = new TextField("Viana");
        cityField.setPromptText("Viana");

        grid.add(new Label("Tipo:"), 0, 0);
        grid.add(typeField, 1, 0);

        grid.add(new Label("CPF:"), 2, 0);
        grid.add(cpfField, 3, 0);

        grid.add(new Label("RG:"), 4, 0);
        grid.add(rgField, 5, 0);

        grid.add(new Label("Nome:"), 0, 1);
        grid.add(nameField, 1, 1);

        grid.add(new Label("Fone:"), 2, 1);
        grid.add(phoneField, 3, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        grid.add(new Label("Nascimento:"), 2, 2);
        grid.add(datePicker, 3, 2); // Adicionar DatePicker ao grid

        Label locationLabel = new Label("Localização");
        locationLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane locationGrid = new GridPane();
        locationGrid.setPadding(new Insets(10, 0, 0, 0));
        locationGrid.setHgap(10);
        locationGrid.setVgap(10);

        locationGrid.add(new Label("Endereço:"), 0, 0);
        locationGrid.add(addressField, 1, 0, 3, 1);

        locationGrid.add(new Label("N°:"), 4, 0);
        locationGrid.add(numberField, 5, 0);

        locationGrid.add(new Label("Complemento:"), 0, 1);
        locationGrid.add(complementField, 1, 1, 5, 1);

        locationGrid.add(new Label("País:"), 0, 2);
        locationGrid.add(countryField, 1, 2);

        locationGrid.add(new Label("Estado:"), 2, 2);
        locationGrid.add(stateField, 3, 2);

        locationGrid.add(new Label("Município:"), 4, 2);
        locationGrid.add(cityField, 5, 2);

        HBox hospedadoBox = new HBox(10);
        hospedadoBox.setAlignment(Pos.CENTER_LEFT);
        Label hospedadoLabel = new Label("Está Hospedado?");
        ToggleGroup hospedadoGroup = new ToggleGroup();
        RadioButton simRadio = new RadioButton("Sim");
        RadioButton naoRadio = new RadioButton("Não");
        simRadio.setToggleGroup(hospedadoGroup);
        naoRadio.setToggleGroup(hospedadoGroup);
        simRadio.setSelected(true);

        hospedadoBox.getChildren().addAll(hospedadoLabel, simRadio, naoRadio);

        Button btnAdicionar = new Button("Cadastrar Pessoa");
        btnAdicionar.setCursor(Cursor.HAND);

        Button btnLimpar = new Button("Limpar Campos");
        btnLimpar.setCursor(Cursor.HAND);

        btnLimpar.setOnAction(e -> {
            typeField.clear();
            cpfField.clear();
            rgField.clear();
            nameField.clear();
            phoneField.clear();
            emailField.clear();
            datePicker.setValue(null);
            addressField.clear();
            numberField.clear();
            complementField.clear();
            countryField.clear();
            stateField.clear();
            cityField.clear();
            simRadio.setSelected(false);
            naoRadio.setSelected(false);
        });

        btnAdicionar.setOnAction(e -> {
                LocalDate dateOfBirth = datePicker.getValue();

                if (dateOfBirth != null) {
                    // adiciona campos que nao podem ser nulos
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Por favor, selecione uma data de nascimento.");
                    alert.showAndWait();
                }
                    salvarDadosPessoa(new PersonRequest(
                            cpfField.getText(),
                            rgField.getText(),
                            nameField.getText(),
                            phoneField.getText(),
                            emailField.getText(),
                            dateOfBirth.toString(),
                            addressField.getText(),
                            numberField.getText(),
                            complementField.getText(),
                            countryField.getText(),
                            stateField.getText(),
                            cityField.getText(),
                            simRadio.isSelected()),
                            btnLimpar);

        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.getChildren().addAll(btnAdicionar, btnLimpar);

        root.getChildren().addAll(sectionLabel, grid, locationLabel, locationGrid, hospedadoBox, buttonBox);

        Scene scene = new Scene(root, 715, 420);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void salvarDadosPessoa(PersonRequest request, Button clearButton) {
        try (Connection connection = PostgresDatabaseConnect.connect()) {
            if (connection != null) {
                String query = """
                insert into customers (nome, email, cpf, phone, datanascimento) VALUES (?,?,?,?,?);
                """;

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, request.name());
                    statement.setString(2, request.email());
                    statement.setString(3, request.cpf());
                    statement.setString(4, request.phone());

                    LocalDate dateOfBirth = LocalDate.parse(request.dateOfBirth());
                    Date sqlDateOfBirth = Date.valueOf(dateOfBirth);

                    statement.setDate(5, sqlDateOfBirth);

                    int rowsInserted = statement.executeUpdate();

                    if (rowsInserted > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Confirmação");
                        alert.setHeaderText(null);
                        alert.setContentText("Pessoa cadastrada com sucesso!");
                        alert.showAndWait();
                        clearButton.fire();
                    }
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao cadastrar pessoas: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }


    public record PersonRequest(
            String cpf,
            String rg,
            String name,
            String phone,
            String email,
            String dateOfBirth,
            String address,
            String number,
            String complement,
            String country,
            String state,
            String city,
            boolean isHospedado
    ) {}



//    public static void main(String[] args) {
//        launch(args);
//    }
}

