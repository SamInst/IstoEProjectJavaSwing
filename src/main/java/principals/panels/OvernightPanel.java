package principals.panels;


import enums.StatusQuartoEnum;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import repository.PernoitesRepository;
import repository.PessoaRepository;
import repository.PrecosRepository;
import repository.QuartosRepository;
import request.BuscaPessoaRequest;
import request.PernoiteRequest;
import response.QuartoResponse;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OvernightPanel extends javax.swing.JPanel {
    PessoaRepository pessoaRepository = new PessoaRepository();
    PernoitesRepository pernoitesRepository = new PernoitesRepository();
    PrecosRepository precosRepository = new PrecosRepository();
    QuartosRepository quartosRepository = new QuartosRepository();
    List<Long> selectedPessoas = new ArrayList<>(); // Lista de IDs das pessoas selecionadas
    Long quartoIdSelecionado = null; // Armazena o ID do quarto selecionado

    // Variável para a lista de pessoas selecionadas
    private JList<String> listaPessoasSelecionadas;

    public OvernightPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.ORANGE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.ORANGE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));

        JButton adicionar = new JButton("Adicionar Pernoite");

        adicionar.addActionListener(e -> abrirJanelaAdicionarPernoite());

        buttonPanel.add(adicionar);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(topPanel, BorderLayout.NORTH);
    }

    // Método para abrir a nova janela para adicionar pernoite
    public void abrirJanelaAdicionarPernoite() {
        JFrame janelaAdicionar = new JFrame("Adicionar Pernoite");
        janelaAdicionar.setSize(800, 600);
        janelaAdicionar.setLayout(new BoxLayout(janelaAdicionar.getContentPane(), BoxLayout.Y_AXIS));

        // 1º Bloco: Escolha do quarto e seleção de datas
        JPanel blocoQuarto = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blocoQuarto.setBorder(BorderFactory.createTitledBorder("Quarto e Datas"));

        JButton botaoEscolherQuarto = new JButton("Escolher Quarto");
        botaoEscolherQuarto.addActionListener(e -> escolherQuarto(janelaAdicionar));
        blocoQuarto.add(botaoEscolherQuarto);

        JLabel labelEntrada = new JLabel("Data Entrada:");
        JDatePickerImpl dataEntrada = null;
        JLabel labelSaida = new JLabel("Data Saída:");
        JDatePickerImpl dataSaida = null;

        blocoQuarto.add(labelEntrada);
        blocoQuarto.add(dataEntrada);
        blocoQuarto.add(labelSaida);
        blocoQuarto.add(dataSaida);

        janelaAdicionar.add(blocoQuarto);

        // 2º Bloco: Valor da diária
        JPanel blocoPreco = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blocoPreco.setBorder(BorderFactory.createTitledBorder("Valor Diária"));

        JLabel valorDiariaLabel = new JLabel("Valor Diária: ");
        blocoPreco.add(valorDiariaLabel);

        JButton descontoButton = new JButton("Desconto");
        blocoPreco.add(descontoButton); // Placeholder para a lógica de desconto

        janelaAdicionar.add(blocoPreco);

        // 3º Bloco: Buscar e adicionar pessoas
        JPanel blocoBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blocoBusca.setBorder(BorderFactory.createTitledBorder("Buscar Pessoas"));

        JTextField campoBusca = new JTextField(15);
        JButton botaoBuscarPessoa = new JButton("Buscar");
        blocoBusca.add(campoBusca);
        blocoBusca.add(botaoBuscarPessoa);

        JList<String> listaPessoas = new JList<>();
        blocoBusca.add(new JScrollPane(listaPessoas));

        // Botão "Adicionar Pessoa"
        JButton adicionarPessoaButton = new JButton("Adicionar Pessoa");
        adicionarPessoaButton.addActionListener(e -> adicionarPessoa(listaPessoas, listaPessoasSelecionadas));
        blocoBusca.add(adicionarPessoaButton);

        janelaAdicionar.add(blocoBusca);

        // 4º Bloco: Lista de pessoas selecionadas
        JPanel blocoPessoasSelecionadas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blocoPessoasSelecionadas.setBorder(BorderFactory.createTitledBorder("Pessoas Selecionadas"));

        // Inicialização da lista de pessoas selecionadas
        listaPessoasSelecionadas = new JList<>(new DefaultListModel<>());
        blocoPessoasSelecionadas.add(new JScrollPane(listaPessoasSelecionadas));

        janelaAdicionar.add(blocoPessoasSelecionadas);

        // Botão de Adicionar Pernoite
        JButton confirmarPernoiteButton = new JButton("Adicionar Pernoite");
        confirmarPernoiteButton.addActionListener(e -> {
            LocalDate dataEntradaSelecionada = (LocalDate) dataEntrada.getModel().getValue();
            LocalDate dataSaidaSelecionada = (LocalDate) dataSaida.getModel().getValue();
            int quantidadePessoas = listaPessoasSelecionadas.getModel().getSize();

            // Atualizar o valor da diária dinamicamente
            float valorDiaria = precosRepository.precoDiaria(quantidadePessoas);
            valorDiariaLabel.setText("Valor Diária: R$ " + valorDiaria);

            List<Long> selectedPessoas = new ArrayList<>();
            for (int i = 0; i < listaPessoasSelecionadas.getModel().getSize(); i++) {
                String pessoa = listaPessoasSelecionadas.getModel().getElementAt(i);
                selectedPessoas.add(Long.valueOf(pessoa.split(" - ")[0])); // Extrair o ID da pessoa
            }

            PernoiteRequest request = new PernoiteRequest(
                    quartoIdSelecionado, // Usar o ID do quarto selecionado
                    dataEntradaSelecionada,
                    dataSaidaSelecionada,
                    quantidadePessoas,
                    selectedPessoas
            );

            pernoitesRepository.adicionarPernoite(request);
        });
        janelaAdicionar.add(confirmarPernoiteButton);

        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);
    }

    // Função para criar o calendário
//    private JDatePickerImpl criarCalendario() {
//        UtilDateModel model = new UtilDateModel();
//        Properties p = new Properties();
//        p.put("text.today", "Hoje");
//        p.put("text.month", "Mês");
//        p.put("text.year", "Ano");
//        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
//        return new JDatePickerImpl(datePanel, new RelatoriosPanel.DateLabelFormatter());
//    }

    // Função de buscar e adicionar pessoas
    private void buscarPessoa(String searchText, JList<String> listaPessoas) {
        List<BuscaPessoaRequest> pessoas = pessoaRepository.buscarPessoaPorIdNomeOuCpf(searchText);
        DefaultListModel<String> model = new DefaultListModel<>();
        for (BuscaPessoaRequest pessoa : pessoas) {
            model.addElement(pessoa.id() + " - " + pessoa.nome());
        }
        listaPessoas.setModel(model);
    }

    // Função para adicionar a pessoa selecionada na lista de pessoas
    private void adicionarPessoa(JList<String> listaPessoas, JList<String> listaPessoasSelecionadas) {
        DefaultListModel<String> selectedModel = (DefaultListModel<String>) listaPessoasSelecionadas.getModel();
        for (String pessoa : listaPessoas.getSelectedValuesList()) {
            selectedModel.addElement(pessoa);
        }
    }

    // Função para escolher o quarto
    private void escolherQuarto(JFrame parentFrame) {
        List<QuartoResponse> quartosDisponiveis = quartosRepository.buscaQuartosPorStatus(StatusQuartoEnum.DISPONIVEL);
        DefaultListModel<String> model = new DefaultListModel<>();
        for (QuartoResponse quarto : quartosDisponiveis) {
            model.addElement("Quarto " + quarto.quarto_id() + " - " + quarto.descricao());
        }

        JList<String> listaQuartos = new JList<>(model);
        int result = JOptionPane.showConfirmDialog(parentFrame, new JScrollPane(listaQuartos), "Selecionar Quarto", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && !listaQuartos.isSelectionEmpty()) {
            String selectedQuarto = listaQuartos.getSelectedValue();
            quartoIdSelecionado = Long.valueOf(selectedQuarto.split(" ")[1]); // Extrair o ID do quarto
            System.out.println("Quarto selecionado: " + quartoIdSelecionado);
        }
    }
}

