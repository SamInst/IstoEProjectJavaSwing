package principals.panels.pernoitePanels;

import enums.StatusPernoiteEnum;
import principals.panels.pessoaPanel.IdentificacaoPessoaFrame;
import principals.tools.Refreshable;
import repository.PernoitesRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static buttons.Botoes.*;
import static principals.tools.CorPersonalizada.*;
import static principals.tools.FormatarFloat.format;
import static principals.tools.Icones.*;
import static principals.tools.Resize.resizeIcon;

public class NewPernoitesPanel extends JPanel implements Refreshable {
    private final PernoitesRepository pernoitesRepository;
    Color cor = BLUE;
    String statusTitulo = "";
    private final Font font = new Font("Arial", Font.BOLD, 20);

    public NewPernoitesPanel(PernoitesRepository pernoitesRepository) {
        this.pernoitesRepository = pernoitesRepository;
        refreshPanel();
    }

    public void pernoites(JPanel relatoriosDoDiaPanel, StatusPernoiteEnum statusPernoiteEnum) {
        pernoitesRepository.buscaPernoitesPorStatus(statusPernoiteEnum).forEach(pernoite -> {
            JButton relatorioButton;
            relatorioButton = new JButton();
            relatorioButton.setBackground(Color.WHITE);
            relatorioButton.setBorderPainted(false);
            relatorioButton.setFocusPainted(false);
            relatorioButton.setContentAreaFilled(true);
            relatorioButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JPanel buttonContent = new JPanel(new BorderLayout());
            buttonContent.setBackground(Color.WHITE);

            JPanel verticalPanel = new JPanel();
            verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
            verticalPanel.setPreferredSize(new Dimension(150, 30));

            var btn_quarto = btn_cinza(pernoite.quarto() < 10 ?
                    " 0" + pernoite.quarto() + " " : " " + pernoite.quarto() + " ");
            btn_quarto.setFont(font);
            btn_quarto.setBackground(cor);

            var btn_data_entrada = btn_branco(pernoite.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            var btn_data_saida = btn_branco(pernoite.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            var btn_icone_qtd_diarias = btn_branco(pernoite.quantidade_diarias().toString());
            btn_icone_qtd_diarias.setIcon(resizeIcon(diarias_quantidade, 15, 15));

            var btn_icone_qtd_pessoas = btn_branco(pernoite.quantidade_pessoas().toString());
            btn_icone_qtd_pessoas.setIcon(resizeIcon(usuarios, 15, 15));

            var btn_icone_qtd_consumo = btn_branco(pernoite.quantidade_consumo().toString());
            btn_icone_qtd_consumo.setIcon(resizeIcon(sacola, 15, 15));

            var nome_representante = btn_branco(pernoite.representante() == null ? "" : pernoite.representante().nome());
            verticalPanel.add(nome_representante);
            verticalPanel.add(btn_quarto);

            var valor = btn_verde("R$ " + format(pernoite.valor_total()));

            if (pernoite.representante() != null) {
                nome_representante.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new IdentificacaoPessoaFrame(pernoite.representante().cpf(), true);
                    }
                });
            }

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(btn_quarto);
            leftPanel.add(btn_data_entrada);
            leftPanel.add(btn_data_saida);
            leftPanel.add(nome_representante);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);
            rightPanel.setBackground(Color.WHITE);
            rightPanel.add(btn_icone_qtd_diarias);
            rightPanel.add(btn_icone_qtd_pessoas);
            rightPanel.add(btn_icone_qtd_consumo);
            rightPanel.add(valor);

            buttonContent.add(leftPanel, BorderLayout.WEST);
            buttonContent.add(rightPanel, BorderLayout.EAST);

            relatorioButton.add(buttonContent, BorderLayout.CENTER);

            relatoriosDoDiaPanel.add(relatorioButton);
        });
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        verificaDiariasEncerradas();

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel identificadorPanel = new JPanel();
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        identificadorPanel.setBorder(BorderFactory.createEmptyBorder(7, 5, 5, 5));

        topPanel.add(identificadorPanel);

        var btnPesquisar = btn_branco("Pesquisar");
        btnPesquisar.setIcon(resizeIcon(search, 15, 15));

        btnPesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        var btnAdicionar = btn_verde("Adicionar Pernoite");
        btnAdicionar.setIcon(resizeIcon(plus, 15, 15));
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            new BuscaPernoiteIndividual();
            }
        });

        JPanel sumarioPanel = new JPanel();
        identificadorPanel.add(btnPesquisar);
        identificadorPanel.add(btnAdicionar);
        identificadorPanel.add(sumarioPanel);

        var hospedados = btn_azul("Hospedados: " + pernoitesRepository.hospedados());
        hospedados.setFont(font);
        hospedados.setPreferredSize(new Dimension(200, 50));

        topPanel.add(hospedados, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);

        JPanel relatoriosPanel = new JPanel();
        relatoriosPanel.setLayout(new BoxLayout(relatoriosPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < StatusPernoiteEnum.values().length; i++) {
            var statusPernoiteEnum = StatusPernoiteEnum.values()[i];
            JPanel relatorioDiaPanel = new JPanel();

            switch (statusPernoiteEnum) {
                case ATIVO -> statusTitulo = "HOSPEDADOS";
                case DIARIA_ENCERRADA -> { statusTitulo = "DIÁRIA ENCERRADA"; cor = RED_4; }
                case CANCELADOS -> { statusTitulo = "CANCELADOS"; cor = ORANGE;
                }
                default -> { statusTitulo = "FINALIZADOS"; cor = GRAY; }
            }
            relatorioDiaPanel.setBackground(BACKGROUND_GRAY);
            relatorioDiaPanel.setLayout(new BorderLayout());
            relatorioDiaPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            var data = btn_branco(statusTitulo);

            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(cor);
            headerPanel.add(data, BorderLayout.WEST);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 10));
            relatorioDiaPanel.add(headerPanel, BorderLayout.NORTH);

            JPanel relatoriosDoDiaPanel = new JPanel();
            relatoriosDoDiaPanel.setLayout(new GridLayout(0, 1, 0, 1));
            relatoriosDoDiaPanel.setBackground(LIGHT_GRAY);
            relatoriosDoDiaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            relatoriosDoDiaPanel.setPreferredSize(null);
            relatoriosDoDiaPanel.setMinimumSize(null);
            relatoriosDoDiaPanel.setMaximumSize(null);

            pernoites(relatoriosDoDiaPanel, statusPernoiteEnum);

            relatorioDiaPanel.add(relatoriosDoDiaPanel, BorderLayout.CENTER);
            relatoriosPanel.add(relatorioDiaPanel);
        }

        JScrollPane scrollPane = new JScrollPane(relatoriosPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void verificaDiariasEncerradas() {
        pernoitesRepository.buscaPernoitesPorStatus(StatusPernoiteEnum.ATIVO)
                .forEach(pernoite -> {
                    if (LocalDateTime.of(pernoite.data_saida(), LocalTime.of(12, 0)).isBefore(LocalDateTime.now())) {
                        pernoitesRepository.alterarStatusPernoite(StatusPernoiteEnum.DIARIA_ENCERRADA, pernoite.pernoite_id());
                    }
                });
    }

    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }
}
