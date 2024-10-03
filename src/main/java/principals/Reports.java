package principals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Reports {

    public static void reports(Stage primaryStage) {
        primaryStage.setTitle("Relatórios");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F9F9F9;");

        HBox headerBox = createHeader();

        List<RelatorioDiario> relatorios = criarRelatorios();

        VBox relatorioDiarioBox = new VBox(10);
        relatorioDiarioBox.setPadding(new Insets(10));
        for (RelatorioDiario relatorioDiario : relatorios) {
            relatorioDiarioBox.getChildren().add(createRelatorioDiarioBox(relatorioDiario));
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(relatorioDiarioBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root.getChildren().addAll(headerBox, scrollPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private static HBox createHeader() {
        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(10));

        Button btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setPrefWidth(100);

        Button btnNovoRelatorio = new Button("Novo Relatorio");
        btnNovoRelatorio.setOnAction(a->{
            addReport(new Stage());
        });

        btnNovoRelatorio.setPrefWidth(100);

        Label lblTotal = new Label("Total: R$ 1.529,87");
        lblTotal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: green;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(btnPesquisar, btnNovoRelatorio, spacer, lblTotal);
        return headerBox;
    }

    private static VBox createRelatorioDiarioBox(RelatorioDiario relatorioDiario) {
        VBox relatorioDiarioBox = new VBox(10);
        relatorioDiarioBox.setPadding(new Insets(10));
        relatorioDiarioBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: #FFFFFF;");

        // Data do relatório
        Label lblData = new Label(relatorioDiario.getData());
        lblData.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");


        VBox entradasBox = new VBox(5);
        for (RelatorioEntrada entrada : relatorioDiario.getEntradas()) {
            HBox entradaBox = new HBox(10);
            entradaBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5;");
            entradaBox.setPadding(new Insets(5));

            Label lblId = new Label(String.valueOf(entrada.getId()));
            Label lblHora = new Label(entrada.getHora());
            Label lblRelatorio = new Label(entrada.getRelatorio());
            Label lblApartamento = new Label(String.valueOf(entrada.getApartamento()));
            Label lblValor = new Label("R$ " + String.format("%.2f", entrada.getValor()));

            // Ajustar a cor do valor baseado se é positivo ou negativo
            if (entrada.getValor() > 0) {
                lblValor.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else if (entrada.getValor() < 0) {
                lblValor.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                lblValor.setStyle("-fx-text-fill: black; -fx-font-weight: normal;"); // Para valor zero
            }

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            entradaBox.getChildren().addAll(lblId, lblHora, lblRelatorio, spacer, lblApartamento, lblValor);
            entradasBox.getChildren().add(entradaBox);

            // Adicionar eventos de mouse para mudança de cor
            entradaBox.setOnMouseEntered(e -> entradaBox.setStyle("-fx-background-color: #E0F7FA; -fx-border-color: #CCCCCC; -fx-border-radius: 5;"));
            entradaBox.setOnMouseExited(e -> entradaBox.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 5;"));
        }

        relatorioDiarioBox.getChildren().addAll(lblData, entradasBox);
        return relatorioDiarioBox;
    }


    private static List<RelatorioDiario> criarRelatorios() {
        List<RelatorioDiario> relatorios = new ArrayList<>();

        List<RelatorioEntrada> entradas1 = new ArrayList<>();
        entradas1.add(new RelatorioEntrada(3244678, "01:00", "PAGO LANCHE", 2, -20.00)); // Valor negativo
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas1.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        relatorios.add(new RelatorioDiario("26/08/2024", entradas1));

        List<RelatorioEntrada> entradas2 = new ArrayList<>();
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));entradas2.add(new RelatorioEntrada(3244677, "00:24", "ENTRADA NOITE (CARTÃO DE CRÉDITO)", 2, 30.00));
        entradas2.add(new RelatorioEntrada(3244676, "00:24", "ENTRADA NOITE (PIX)", 2, 30.00));



        relatorios.add(new RelatorioDiario("25/08/2024", entradas2));

        return relatorios;
    }


    public static void addReport(Stage primaryStage) {
        primaryStage.setTitle("Adicionar Relatório");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER_LEFT);

        Label lblRelatorio = new Label("Relatório:");
        TextField txtRelatorio = new TextField();
        txtRelatorio.setPromptText("Relatório");
        txtRelatorio.setPrefWidth(250);

        Label lblPagamento = new Label("Pagamento:");
        TextField txtPagamento = new TextField();
        txtPagamento.setPromptText("Pagamento");

        Label lblApt = new Label("Apt:");
        TextField txtApt = new TextField();
        txtApt.setPromptText("Apt");
        txtApt.setPrefWidth(60);

        Label lblValor = new Label("Valor: R$");
        TextField txtValor = new TextField();
        txtValor.setPromptText("Valor");

        grid.add(lblRelatorio, 0, 0);
        grid.add(txtRelatorio, 1, 0, 2, 1);

        grid.add(lblPagamento, 0, 1);
        grid.add(txtPagamento, 1, 1);

        grid.add(lblApt, 2, 1);
        grid.add(txtApt, 4, 1);

        HBox valorBox = new HBox(10);
        valorBox.setAlignment(Pos.BASELINE_RIGHT);

        Button btnAdicionar = new Button("Adicionar");
        btnAdicionar.setGraphic(new Label("+"));

        Button btnSubtrair = new Button("Subtrair");
        btnSubtrair.setGraphic(new Label("-"));

        valorBox.getChildren().addAll(lblValor, txtValor, btnAdicionar, btnSubtrair);

        root.getChildren().addAll(grid, valorBox);

        Scene scene = new Scene(root, 420, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static class RelatorioDiario {
        private String data;
        private List<RelatorioEntrada> entradas;

        public RelatorioDiario(String data, List<RelatorioEntrada> entradas) {
            this.data = data;
            this.entradas = entradas;
        }

        public String getData() {
            return data;
        }

        public List<RelatorioEntrada> getEntradas() {
            return entradas;
        }
    }

    public static class RelatorioEntrada {
        private int id;
        private String hora;
        private String relatorio;
        private int apartamento;
        private double valor;

        public RelatorioEntrada(int id, String hora, String relatorio, int apartamento, double valor) {
            this.id = id;
            this.hora = hora;
            this.relatorio = relatorio;
            this.apartamento = apartamento;
            this.valor = valor;
        }

        public int getId() {
            return id;
        }

        public String getHora() {
            return hora;
        }

        public String getRelatorio() {
            return relatorio;
        }

        public int getApartamento() {
            return apartamento;
        }

        public double getValor() {
            return valor;
        }
    }
}
