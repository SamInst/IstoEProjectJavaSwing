package org.sam.istoeproject.modals;

import config.PostgresDatabaseConnect;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnterpriseIdentification {
    private static List<Person> selectedPersons;
    private static VBox selectedCompanyBox;
    private static Button btnVincular;

    public static void enterpriseIdentificationModal(Stage primaryStage) {
        primaryStage.setTitle("Identificação de Empresa");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #CCCCCC; -fx-border-radius: 15; -fx-background-radius: 15;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        Label sectionLabel = new Label("Identificação de Empresa");
        sectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 0, 0, 0));
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints(150);
        ColumnConstraints col2 = new ColumnConstraints(200);
        ColumnConstraints col3 = new ColumnConstraints(150);
        ColumnConstraints col4 = new ColumnConstraints(200);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

//        ComboBox<TypeEnum> typeComboBox = new ComboBox<>();
//        typeComboBox.getItems().addAll(TypeEnum.values());
//        typeComboBox.setPrefWidth(180);

        TextField cnpjField = new TextField();
        cnpjField.setPromptText("52.006.953/0001-60");
        cnpjField.setPrefWidth(250);

        grid.add(new Label("Tipo:"), 0, 0);
//        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("CNPJ:"), 2, 0);
        grid.add(cnpjField, 3, 0);

        TextField nameField = new TextField();
        nameField.setPromptText("Isto E Pousada");
        nameField.setPrefWidth(400);

        grid.add(new Label("Nome/Razao Social:"), 0, 1);
        grid.add(nameField, 1, 1, 3, 1);

        TextField addressField = new TextField();
        addressField.setPromptText("Rodovia MA 014 KM 38");
        addressField.setPrefWidth(300);

        TextField numberField = new TextField();
        numberField.setPromptText("612");

        grid.add(new Label("Endereço:"), 0, 2);
        grid.add(addressField, 1, 2, 2, 1);
        grid.add(new Label("N°:"), 3, 2);
        grid.add(numberField, 3, 2);

        TextField countryField = new TextField("Brasil");
        countryField.setPrefWidth(150);

        TextField stateField = new TextField("Maranhão");
        stateField.setPrefWidth(150);

        TextField countyField = new TextField("Viana");
        countyField.setPrefWidth(150);

        grid.add(new Label("País:"), 0, 3);
        grid.add(countryField, 1, 3);

        grid.add(new Label("Estado:"), 2, 3);
        grid.add(stateField, 3, 3);

        grid.add(new Label("Município:"), 4, 3);
        grid.add(countyField, 5, 3);

        Label employeeSection = new Label("Pessoas associadas");
        employeeSection.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox employeeBox = new VBox(10);
        employeeBox.setPadding(new Insets(10, 20, 0, 20));

        employeeBox.getChildren().addAll(
                createEmployeeItem("#15489", "SARA BELA NUNES DINIZ", employeeBox),
                createEmployeeItem("#15489", "Sam Helson Nunes Diniz", employeeBox),
                createEmployeeItem("#15489", "SARA BELA NUNES DINIZ", employeeBox),
                createEmployeeItem("#15489", "Sam Helson Nunes Diniz", employeeBox)
        );

        scrollPane.setContent(employeeBox);
        scrollPane.setPrefViewportHeight(120);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button btnSave = new Button("Salvar");
        Button btnAddPerson = new Button("Adicionar Pessoa");

        btnAddPerson.setOnAction(e -> {
            linkUserToEnterprise(new Stage());
        });

        buttonBox.getChildren().addAll(btnAddPerson, btnSave);
        root.getChildren().addAll(sectionLabel, grid, employeeSection, scrollPane, buttonBox);

        Scene scene = new Scene(root, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static HBox createEmployeeItem(String id, String name, VBox employeeBox) {
        HBox itemBox = new HBox(10);
        itemBox.setAlignment(Pos.CENTER_LEFT);

        Image iconImage = new Image("C:\\Users\\sanhe\\IdeaProjects\\IstoEProject\\src\\main\\resources\\icons\\linked.png");
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(16);
        iconView.setFitHeight(16);

        Label idLabel = new Label(id);
        idLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 14px;");

        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: red;");

        removeButton.setOnAction(e -> {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Remove Person");
            confirmationAlert.setContentText("Are you sure you want to remove " + name + "?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == yesButton) {
                    employeeBox.getChildren().remove(itemBox);
                }
            });
        });

        itemBox.getChildren().addAll(iconView, idLabel, nameLabel, removeButton);
        return itemBox;
    }

    private static void linkUserToEnterprise(Stage primaryStage) {
        primaryStage.setTitle("Vincular Pessoa");

        selectedPersons = new ArrayList<>();

        TextField searchField = new TextField();
        searchField.setPromptText("Nome/Razao Social");

        ListView<HBox> searchResults = new ListView<>();
        searchResults.setPrefHeight(150);

        selectedCompanyBox = new VBox(10);
        selectedCompanyBox.setPadding(new Insets(10));

        btnVincular = new Button("Vincular");
        Button btnSaveLinkedPerson = new Button("Salvar");
        btnVincular.setDisable(true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Person> filteredPersons = findPerson(newValue);
            searchResults.getItems().clear();
            for (Person person : filteredPersons) {
                HBox personItem = createSearchResultItem(person);
                searchResults.getItems().add(personItem);
            }
        });

        btnVincular.setOnAction(e -> {
            for (HBox item : searchResults.getItems()) {
                CheckBox checkBox = (CheckBox) item.getChildren().get(2);
                if (checkBox.isSelected()) {
                    Label personLabel = new Label(((Label) item.getChildren().get(1)).getText());
                    Button removeButton = new Button("❌");
                    HBox selectedItem = new HBox(10, personLabel, removeButton);
                    selectedCompanyBox.getChildren().add(selectedItem);
                    removeButton.setOnAction(ev -> selectedCompanyBox.getChildren().remove(selectedItem));
                }
            }
            btnVincular.setDisable(true);
        });

        btnSaveLinkedPerson.setOnAction(e -> {
            selectedPersons.forEach(person -> {
                createEmployeeItem(person.getId(), person.getName(), new VBox());
            });
        });

        VBox root = new VBox(15, searchField, searchResults, btnVincular, selectedCompanyBox, btnSaveLinkedPerson);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static HBox createSearchResultItem(Person person) {
        Label idLabel = new Label(person.getId());
        Label nameLabel = new Label(person.getName() + "\n" + person.getCpf());
        CheckBox checkBox = new CheckBox();

        HBox hBox = new HBox(10, idLabel, nameLabel, checkBox);
        hBox.setAlignment(Pos.CENTER_LEFT);

        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()) {
                btnVincular.setDisable(false);
            }
        });

        return hBox;
    }

    private static List<Person> findPerson(String nome) {
        List<Person> persons = new ArrayList<>();

        try (Connection connection = PostgresDatabaseConnect.connect()) {
            if (connection != null) {
                String query = """
                    select
                        id,
                        nome,
                        cpf
                    from customers
                    where nome ilike ?;
                    """;

                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, "%" + nome + "%");

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            String id = resultSet.getString("id");
                            String name = resultSet.getString("nome");
                            String cpf = resultSet.getString("cpf");
                            Person person = new Person(id, name, cpf);
                            persons.add(person);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persons;
    }

    public static class Person {
        private String id;
        private String name;
        private String cpf;

        public Person(String id, String name, String cpf) {
            this.id = id;
            this.name = name;
            this.cpf = cpf;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCpf() {
            return cpf;
        }
    }
}
