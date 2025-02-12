package menu.panels.quartosPanel;

import enums.StatusQuartoEnum;
import repository.QuartosRepository;
import response.QuartoResponse;
import tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Objects;

public class ListaDeQuartosJPanel {
    int largura = 25;
    int altura = 25;
    Font font = new Font("Roboto", Font.BOLD, 18);
    Color cor = CorPersonalizada.DARK_BLUE;

    public JPanel mainPanel(QuartosRepository quartosRepository, RoomsPanel roomsPanel) {
        var quartos = quartosRepository.buscaTodosOsQuartos();
        quartos.sort(Comparator.comparingLong(QuartoResponse::quarto_id));

        JPanel quartoPanel = new JPanel();
        quartoPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));
        quartoPanel.setBackground(CorPersonalizada.LIGHT_GRAY);
        quartoPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,0));

        for (QuartoResponse quarto : quartos) {
            JLabel icone_rede = new JLabel(Resize.resizeIcon(Icones.rede, largura, altura));
            JLabel icone_cama_casal = new JLabel(Resize.resizeIcon(Icones.cama_casal, largura, altura));
            JLabel icone_cama_solteiro = new JLabel(Resize.resizeIcon(Icones.cama_solteiro, largura, altura));
            JLabel icone_beliche = new JLabel(Resize.resizeIcon(Icones.beliche, largura, altura));
            JLabel icone_qtd_pessoas = new JLabel(Resize.resizeIcon(Icones.usuarios, largura, altura));

            JTextFieldComTextoFixoArredondado categoria = new JTextFieldComTextoFixoArredondado("", 0);
            categoria.setFont(new Font("Roboto", Font.BOLD, 15));
            categoria.setBackground(CorPersonalizada.LIGHT_GRAY);

            JComboBoxArredondado<StatusQuartoEnum> statusQuartoComboBox = new JComboBoxArredondado<>();
            statusQuartoComboBox.setEditable(false);
            statusQuartoComboBox.setPreferredSize(new Dimension(200, 30));

            JTextFieldComTextoFixoArredondadoRelatorios tabelaPreco = new JTextFieldComTextoFixoArredondadoRelatorios("$",0);
            tabelaPreco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            tabelaPreco.setFont(new Font("Roboto", Font.BOLD, 20));
            tabelaPreco.setBackground(CorPersonalizada.DARK_GREEN);
            tabelaPreco.setEnabled(false);
            tabelaPreco.setForeground(Color.white);

            tabelaPreco.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tabelaPreco.setBackground(CorPersonalizada.DARK_GREEN.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tabelaPreco.setBackground(CorPersonalizada.DARK_GREEN);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    new TabelaPrecoPorQuartoFrame(quarto.categoria().valorPessoaList());
                }
            });

            BotaoArredondado quartoButton = new BotaoArredondado("");
            quartoButton.setPreferredSize(new Dimension(405, 150));
            quartoButton.setBackground(Color.WHITE);
            quartoButton.setBorderPainted(false);
            quartoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            quartoButton.setLayout(new BorderLayout());
            quartoButton.setBorder(BorderFactory.createEmptyBorder(15,15,0,15));

            quartoButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(true, cor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((BotaoArredondado) e.getSource()).setShowBorder(false, Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    new AdicionarQuartoFrame(quartosRepository, quarto.quarto_id(), roomsPanel);
                }
            });

            JPanel panelInterno = new JPanel(new BorderLayout());
            panelInterno.setBackground(Color.WHITE);

            PanelArredondado panelVermelho = new PanelArredondado();
            panelVermelho.setBackground(Color.WHITE);
            panelVermelho.setForeground(Color.DARK_GRAY);

            JLabel numeroQuarto = new JLabel(quarto.quarto_id() < 10 ? "0" + quarto.quarto_id() : quarto.quarto_id() + "");
            numeroQuarto.setForeground(CorPersonalizada.LIGHT_GRAY);
            numeroQuarto.setFont(new Font("Roboto", Font.BOLD, 45));

            panelVermelho.setPreferredSize(new Dimension(90, 0));
            panelVermelho.add(numeroQuarto);
            panelInterno.add(panelVermelho, BorderLayout.WEST);

            JPanel panelBranco = new JPanel(new BorderLayout());
            panelBranco.setBackground(Color.WHITE);

            JPanel panelVerde = new JPanel(new BorderLayout());
            panelVerde.setBackground(Color.WHITE);
            panelVerde.setPreferredSize(new Dimension(0, 40));
            panelVerde.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
            panelBranco.add(panelVerde, BorderLayout.NORTH);

            panelVerde.add(statusQuartoComboBox,  BorderLayout.WEST);
            panelVerde.add(tabelaPreco, BorderLayout.EAST);

            JPanel panelLaranja = new JPanel(new BorderLayout());
            panelLaranja.setBackground(Color.WHITE);
            panelLaranja.setPreferredSize(new Dimension(0, 140));
            panelLaranja.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

           if (quarto.categoria() != null){

               int categoriaLength = quarto.categoria().categoria().length();
               int columnSize = (int) (categoriaLength * 0.75);
               categoria.setColumns(columnSize);

               categoria.setHorizontalAlignment(JTextField.CENTER);
               categoria.setText(quarto.categoria().categoria());
               categoria.setColumns(columnSize);
               categoria.setForeground(Color.WHITE);
           }
            panelLaranja.add(categoria, BorderLayout.WEST);

            panelBranco.add(panelLaranja, BorderLayout.CENTER);


            panelInterno.add(panelBranco, BorderLayout.CENTER);

            JPanel panelAzul = new JPanel(new BorderLayout());
            panelAzul.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
            panelAzul.setBackground(Color.WHITE);
            panelAzul.setPreferredSize(new Dimension(0, 55));
            quartoButton.add(panelInterno, BorderLayout.CENTER);
            quartoButton.add(panelAzul, BorderLayout.SOUTH);

            JPanel panelAzulSuperior = new JPanel(new BorderLayout());
            panelAzulSuperior.setPreferredSize(new Dimension(0, 2));

            panelAzul.add(panelAzulSuperior, BorderLayout.NORTH);

            JPanel panelAzulEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panelAzulEsquerda.setBackground(Color.WHITE);

            JPanel panelAzulDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            panelAzulDireita.setBackground(Color.WHITE);


            JLabel qtdPessoasLabel = new JLabel(quarto.quantidade_pessoas().toString());
            qtdPessoasLabel.setForeground(CorPersonalizada.DARK_GREEN);
            qtdPessoasLabel.setFont(font);

            panelAzulEsquerda.add(icone_qtd_pessoas);
            panelAzulEsquerda.add(qtdPessoasLabel);

            if (quarto.qtd_cama_casal() > 0) {
                panelAzulDireita.add(icone_cama_casal);
                JLabel numero = new JLabel(quarto.qtd_cama_casal() + " ");
                numero.setForeground(CorPersonalizada.DARK_GREEN);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_cama_solteiro() > 0) {
                panelAzulDireita.add(icone_cama_solteiro);
                JLabel numero = new JLabel(quarto.qtd_cama_solteiro() + " ");
                numero.setForeground(CorPersonalizada.DARK_GREEN);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_rede() > 0) {
                panelAzulDireita.add(icone_rede);
                JLabel numero = new JLabel(quarto.qtd_rede()+ " ");
                numero.setForeground(CorPersonalizada.DARK_GREEN);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            if (quarto.qtd_cama_beliche() > 0) {
                panelAzulDireita.add(icone_beliche);
                JLabel numero = new JLabel(quarto.qtd_cama_beliche()+ " ");
                numero.setForeground(CorPersonalizada.DARK_GREEN);
                numero.setFont(font);
                panelAzulDireita.add(numero);
            }

            panelAzul.add(panelAzulEsquerda, BorderLayout.WEST);
            panelAzul.add(panelAzulDireita, BorderLayout.EAST);

            quartoPanel.add(quartoButton);

            atualizarStatusQuarto(quarto.status_quarto_enum(), statusQuartoComboBox, panelVermelho);

            statusQuartoComboBox.addActionListener(e -> {
                String selectedItem = Objects.requireNonNull(statusQuartoComboBox.getSelectedItem()).toString();
                StatusQuartoEnum novoStatus = StatusQuartoEnum.valueOf(selectedItem);

                quartosRepository.alterarStatusQuarto(quarto.quarto_id(), novoStatus);

                atualizarStatusQuarto(novoStatus, statusQuartoComboBox, panelVermelho);
            });
        }

        return quartoPanel;
    }

    private void atualizarStatusQuarto(StatusQuartoEnum status, JComboBoxArredondado<StatusQuartoEnum> statusQuartoComboBox, JPanel panelVermelho) {
        statusQuartoComboBox.removeAllItems();

        switch (status) {
            case OCUPADO -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.OCUPADO);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.OCUPADO);
                statusQuartoComboBox.setEnabled(false);
                panelVermelho.setBackground(new Color(0xF88E8E));
            }
            case RESERVADO -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.RESERVADO);
                statusQuartoComboBox.addItem(StatusQuartoEnum.DISPONIVEL);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.RESERVADO);
                panelVermelho.setBackground(new Color(0xE7CE8A));
            }
            case DISPONIVEL -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.DISPONIVEL);
                statusQuartoComboBox.addItem(StatusQuartoEnum.LIMPEZA);
                statusQuartoComboBox.addItem(StatusQuartoEnum.MANUTENCAO);
                statusQuartoComboBox.addItem(StatusQuartoEnum.RESERVADO);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.DISPONIVEL);
                panelVermelho.setBackground(new Color(0x9EDFA5));
            }
            case DIARIA_ENCERRADA -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.DIARIA_ENCERRADA);
                statusQuartoComboBox.addItem(StatusQuartoEnum.DISPONIVEL);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.DIARIA_ENCERRADA);
                panelVermelho.setBackground(new Color(0xB65D5D));
            }
            case LIMPEZA -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.LIMPEZA);
                statusQuartoComboBox.addItem(StatusQuartoEnum.MANUTENCAO);
                statusQuartoComboBox.addItem(StatusQuartoEnum.DISPONIVEL);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.LIMPEZA);
                panelVermelho.setBackground(Color.ORANGE);
            }
            case MANUTENCAO -> {
                statusQuartoComboBox.addItem(StatusQuartoEnum.MANUTENCAO);
                statusQuartoComboBox.addItem(StatusQuartoEnum.DISPONIVEL);
                statusQuartoComboBox.setSelectedItem(StatusQuartoEnum.MANUTENCAO);
                panelVermelho.setBackground(new Color(0xA19D9D));
            }
            default -> panelVermelho.setBackground(Color.BLACK);
        }
    }
}

