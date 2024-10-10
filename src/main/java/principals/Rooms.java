package principals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    public static void start(Stage primaryStage) {
        primaryStage.setTitle("Quartos");

        // Criar uma lista de quartos
        List<Quarto> quartos = criarQuartos();

        // Criar a barra superior com dois botões
        HBox topBar = new HBox(10);  // Espaço entre os botões
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button btnPesquisar = new Button("Pesquisar");
        Button btnAdicionar = new Button("Adicionar Quarto");

        btnAdicionar.setOnAction(e -> {
            // Lógica para adicionar um quarto
            Quarto novoQuarto = new Quarto(quartos.size() + 1, StatusQuartoEnum.DISPONIVEL, 2);
            quartos.add(novoQuarto);
            VBox novoQuartoBox = criarQuartoBox(novoQuarto);
            ((TilePane) topBar.getParent().lookup("#tilePane")).getChildren().add(novoQuartoBox);
        });

        topBar.getChildren().addAll(btnPesquisar, btnAdicionar);

        // Configurar o TilePane para organizar os quartos de maneira fluida
        TilePane tilePane = new TilePane();
        tilePane.setId("tilePane");  // Definir um ID para o TilePane para acesso posterior
        tilePane.setPadding(new Insets(20));
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPrefColumns(3); // Número preferido de colunas
        tilePane.setAlignment(Pos.TOP_LEFT); // Alinha os blocos ao topo e à esquerda

        // Adicionar os quartos ao TilePane
        for (Quarto quarto : quartos) {
            VBox quartoBox = criarQuartoBox(quarto);
            tilePane.getChildren().add(quartoBox);
        }

        ScrollPane scrollPane = new ScrollPane(tilePane); // Para garantir que se ajusta em diferentes tamanhos
        scrollPane.setFitToWidth(true);

        // Adicionar a barra superior e o TilePane em um VBox
        VBox root = new VBox(10);
        root.getChildren().addAll(topBar, scrollPane);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para criar o layout visual de um quarto
    private static VBox criarQuartoBox(Quarto quarto) {
        VBox quartoBox = new VBox(10);
        quartoBox.setPadding(new Insets(10));
        quartoBox.setAlignment(Pos.CENTER);
        quartoBox.setPrefWidth(160); // Define uma largura padrão para os blocos de quarto
        quartoBox.setPrefHeight(120); // Define uma altura padrão para os blocos de quarto
        atualizarEstiloQuarto(quartoBox, quarto); // Define o estilo inicial do bloco

        Label lblNumero = new Label(String.format("Quarto %02d", quarto.numero()));
        lblNumero.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;"); // Cor branca para o número do quarto

        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER);
        Label lblPessoas = new Label(String.valueOf(quarto.quantidadePessoa()));
        lblPessoas.setStyle("-fx-font-size: 14px;");
        Label lblIconPessoa = new Label("\uD83D\uDC64");  // Ícone de pessoas
        statusBox.getChildren().addAll(lblIconPessoa, lblPessoas);

        // Criar o botão de status
        Button btnStatus = new Button(quarto.statusQuartoEnum().name());
        atualizarEstiloBotao(btnStatus, quarto.statusQuartoEnum()); // Define a cor do botão conforme o status

        // Cursor de mão ao passar o mouse sobre o botão de status
        btnStatus.setCursor(Cursor.HAND);

        // Alterar a cor do botão de status ao passar o mouse
        btnStatus.setOnMouseEntered(e -> btnStatus.setStyle(btnStatus.getStyle() + "-fx-opacity: 0.8;"));
        btnStatus.setOnMouseExited(e -> atualizarEstiloBotao(btnStatus, quarto.statusQuartoEnum())); // Reseta a cor

        // Criar o ContextMenu com os outros status possíveis
        ContextMenu contextMenu = new ContextMenu();
        for (StatusQuartoEnum status : StatusQuartoEnum.values()) {
            MenuItem item = new MenuItem(status.name());
            item.setOnAction(e -> {
                // Atualizar o status do quarto e o botão
                btnStatus.setText(status.name());
                quarto.setStatusQuartoEnum(status);
                atualizarEstiloQuarto(quartoBox, quarto); // Atualiza as cores do bloco de acordo com o novo status
                atualizarEstiloBotao(btnStatus, status); // Atualiza a cor do botão de status
            });
            contextMenu.getItems().add(item);
        }

        // Exibir o menu de opções ao clicar no botão
        btnStatus.setOnAction(e -> contextMenu.show(btnStatus, btnStatus.getLayoutX(), btnStatus.getLayoutY() + btnStatus.getHeight()));

        // Efeito de hover (passar o mouse) no bloco de quarto
        quartoBox.setOnMouseEntered(e -> {
            quartoBox.setStyle(quartoBox.getStyle() + "-fx-opacity: 0.9;"); // Suavemente muda a cor ao passar o mouse
            quartoBox.setCursor(Cursor.HAND); // Altera o cursor para "HAND"
        });
        quartoBox.setOnMouseExited(e -> {
            atualizarEstiloQuarto(quartoBox, quarto); // Reseta a cor ao sair do hover
        });

        quartoBox.getChildren().addAll(lblNumero, statusBox, btnStatus);
        return quartoBox;
    }

    // Método para atualizar o estilo do bloco do quarto com base no status
    private static void atualizarEstiloQuarto(VBox quartoBox, Quarto quarto) {
        String borderColor, backgroundColor;
        switch (quarto.statusQuartoEnum()) {
            case DISPONIVEL:
                borderColor = "green";
                backgroundColor = "#E0F7FA";
                break;
            case OCUPADO:
                borderColor = "red";
                backgroundColor = "#FFCDD2";
                break;
            case MANUTENCAO:
                borderColor = "orange";
                backgroundColor = "#FFF3E0";
                break;
            default:
                borderColor = "gray";
                backgroundColor = "#F0F0F0";
                break;
        }

        // Atualizar o estilo do VBox com as cores apropriadas
        quartoBox.setStyle(String.format("-fx-border-color: %s; -fx-border-radius: 5; -fx-background-color: %s; -fx-border-width: 2;", borderColor, backgroundColor));
    }

    // Método para atualizar o estilo do botão de status
    private static void atualizarEstiloBotao(Button btnStatus, StatusQuartoEnum status) {
        String backgroundColor;
        switch (status) {
            case DISPONIVEL:
                backgroundColor = "green";
                break;
            case OCUPADO:
                backgroundColor = "red";
                break;
            case MANUTENCAO:
                backgroundColor = "orange";
                break;
            default:
                backgroundColor = "gray";
                break;
        }

        btnStatus.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold;", backgroundColor));
    }

    // Método para criar uma lista de quartos
    private static List<Quarto> criarQuartos() {
        List<Quarto> quartos = new ArrayList<>();
        quartos.add(new Quarto(1, StatusQuartoEnum.DISPONIVEL, 2));
        quartos.add(new Quarto(2, StatusQuartoEnum.OCUPADO, 2));
        quartos.add(new Quarto(3, StatusQuartoEnum.MANUTENCAO, 2));
        quartos.add(new Quarto(4, StatusQuartoEnum.DISPONIVEL, 2));
        quartos.add(new Quarto(5, StatusQuartoEnum.OCUPADO, 2));
        quartos.add(new Quarto(6, StatusQuartoEnum.DISPONIVEL, 2));
        quartos.add(new Quarto(7, StatusQuartoEnum.MANUTENCAO, 2));
        quartos.add(new Quarto(8, StatusQuartoEnum.OCUPADO, 2));
        quartos.add(new Quarto(9, StatusQuartoEnum.DISPONIVEL, 2));
        return quartos;
    }

    // Classe record para representar um quarto
    public static class Quarto {
        private int numero;
        private StatusQuartoEnum statusQuartoEnum;
        private int quantidadePessoa;

        public Quarto(int numero, StatusQuartoEnum statusQuartoEnum, int quantidadePessoa) {
            this.numero = numero;
            this.statusQuartoEnum = statusQuartoEnum;
            this.quantidadePessoa = quantidadePessoa;
        }

        public int numero() {
            return numero;
        }

        public StatusQuartoEnum statusQuartoEnum() {
            return statusQuartoEnum;
        }

        public void setStatusQuartoEnum(StatusQuartoEnum status) {
            this.statusQuartoEnum = status;
        }

        public int quantidadePessoa() {
            return quantidadePessoa;
        }
    }

    // Enum para status do quarto
    public enum StatusQuartoEnum {
        DISPONIVEL, OCUPADO, MANUTENCAO
    }
}
