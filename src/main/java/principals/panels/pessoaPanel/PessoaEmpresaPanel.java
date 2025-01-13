package principals.panels.pessoaPanel;

import buttons.BotaoComSombra;
import buttons.Botoes;
import principals.tools.*;
import repository.EmpresaRepository;
import repository.PessoaRepository;
import textField.TextFieldComSobra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import static principals.tools.Icones.*;
import static principals.tools.ImagemArredodanda.*;
import static principals.tools.Resize.*;

public class PessoaEmpresaPanel extends JPanel implements Refreshable {
    private static final Integer FEMININO = 1;
    TextFieldComSobra textFieldBuscaPorNome = new TextFieldComSobra();
    TextFieldComSobra textFieldBuscaPorCPF = new TextFieldComSobra();


    public PessoaEmpresaPanel() {
        initializePanel();
    }

    private void initializePanel() {
        PessoaRepository pessoaRepository = new PessoaRepository();
        EmpresaRepository empresaRepository = new EmpresaRepository(pessoaRepository);

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        PanelArredondado leftPanel = new PanelArredondado();
        leftPanel.setBackground(Cor.CINZA_CLARO);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setFocusable(false);

        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar verticalScrollBar = leftScrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(20);
        verticalScrollBar.setBlockIncrement(100);

        configureLeftPanel(leftPanel, pessoaRepository, empresaRepository, 0);

        PanelArredondado rightPanel = new PanelArredondado();
        rightPanel.setBackground(Color.GRAY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(leftScrollPane, gbc);

        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void configureLeftPanel(PanelArredondado leftPanel, PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, int page) {
        var pessoas = pessoaRepository.buscarTodasAsPessoasComPaginacao(page);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        BotaoArredondado blocoSuperior = new BotaoArredondado("");
        blocoSuperior.setLayout(new GridBagLayout());

        BotaoComSombra btnAdicionar = Botoes.btn_verde("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(100, 30));
        btnAdicionar.addActionListener(evt -> {
            var identificacaoPessoaFrame = new IdentificacaoPessoaFrame(null, false);
            identificacaoPessoaFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshPanel();
                }
            });
        });

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        blocoSuperior.add(btnAdicionar, gbc);

        textFieldBuscaPorNome.setPreferredSize(new Dimension(200, 40));
        textFieldBuscaPorNome.setPlaceholder("Buscar por nome: ");

        textFieldBuscaPorCPF.setPreferredSize(new Dimension(100, 40));
        textFieldBuscaPorCPF.setPlaceholder("Buscar por CPF: ");

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        blocoSuperior.add(textFieldBuscaPorNome, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(10, 10, 10, 10);
        blocoSuperior.add(textFieldBuscaPorCPF, gbc);

        leftPanel.add(blocoSuperior, gbc);

        for (int i = 0; i < pessoas.size(); i++) {
            var iconePessoa = resizeIcon(usuarios, 20, 20);
            var iconeVinculoEmpresa = resizeIcon(linked, 20, 20);
            var pessoa = pessoas.get(i);

            var empresa = empresaRepository.buscarUltimaEmpresaCadastradaPorCpfPessoa(pessoa.cpf());

            BufferedImage pessoaFoto = null;

            try {
                pessoaFoto = pessoaRepository.buscarFotoBufferedPessoaPorId(pessoa.id());
            } catch (SQLException e) {
                System.out.println("imagem nao encontrada");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            BotaoArredondado blocoPessoaButton = new BotaoArredondado("");
            blocoPessoaButton.setPreferredSize(new Dimension(400, 110));
            blocoPessoaButton.setBackground(Color.LIGHT_GRAY.brighter());
            blocoPessoaButton.setBorderPainted(false);
            blocoPessoaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            blocoPessoaButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            blocoPessoaButton.setLayout(new BorderLayout());

            blocoPessoaButton.setOpaque(false);
            blocoPessoaButton.setContentAreaFilled(false);
            blocoPessoaButton.setFocusPainted(false);


            BotaoArredondado blocoFotoPessoa = new BotaoArredondado("");
            blocoFotoPessoa.setPreferredSize(new Dimension(0, 110));
            blocoFotoPessoa.setBackground(Color.RED);
            blocoFotoPessoa.setBorderPainted(false);

            LabelArredondado labelFotoPessoa = new LabelArredondado("");
            labelFotoPessoa.setBackground(Color.WHITE);

            int largura = 90;
            int altura = 90;

            ImageIcon icon;

            if (pessoaFoto != null) {
                var imagemArredondada = arredondar(pessoaFoto);
                ImageIcon roudedCcon = new ImageIcon(imagemArredondada);
                icon = resizeIcon(roudedCcon, largura, altura);
            } else if (pessoa.sexo().equals(FEMININO)) {
                var image = arredondar(convertImageIconToBufferedImage(user_sem_foto_feminino));
                ImageIcon roudedCcon = new ImageIcon(image);
                icon = resizeIcon(roudedCcon, largura, altura);
            } else {
                var image = arredondar(convertImageIconToBufferedImage(user_sem_foto));
                ImageIcon roudedCcon = new ImageIcon(image);
                icon = resizeIcon(roudedCcon, largura, altura);
            }

            labelFotoPessoa.setIcon(icon);

            JPanel blocoAzul = new JPanel();
            blocoAzul.setBackground(Color.WHITE);
            blocoAzul.setPreferredSize(new Dimension(120, 120));
            blocoAzul.setLayout(new GridBagLayout());
            blocoAzul.add(labelFotoPessoa);

            JPanel painelDireita = new JPanel();
            painelDireita.setLayout(new GridLayout(2, 1));

            JPanel blocoVermelho = new JPanel(new BorderLayout());
            blocoVermelho.setBackground(Color.WHITE);
            blocoVermelho.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

            JPanel painelEsquerdoVermelho = new JPanel(new FlowLayout(FlowLayout.LEFT));
            painelEsquerdoVermelho.setOpaque(false);
            painelEsquerdoVermelho.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

            JLabel iconeUsuarioLabel = new JLabel(iconePessoa);
            JLabel nomePessoaLabel = new JLabel(pessoa.nome());
            nomePessoaLabel.setForeground(Color.DARK_GRAY);
            nomePessoaLabel.setFont(new Font("Roboto", Font.BOLD, 16));
            painelEsquerdoVermelho.add(iconeUsuarioLabel);
            painelEsquerdoVermelho.add(nomePessoaLabel);

            JLabel telefonePessoaLabel = new JLabel(pessoa.telefone());
            telefonePessoaLabel.setForeground(Color.DARK_GRAY);
            telefonePessoaLabel.setFont(new Font("Roboto", Font.BOLD, 16));
            telefonePessoaLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            blocoVermelho.add(painelEsquerdoVermelho, BorderLayout.WEST);
            blocoVermelho.add(telefonePessoaLabel, BorderLayout.EAST);

            JPanel blocoVerde = new JPanel(new BorderLayout());
            blocoVerde.setBackground(Color.WHITE);
            blocoVerde.setPreferredSize(new Dimension(blocoVerde.getWidth(), 40));
            blocoVerde.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if (empresa != null) {
                JPanel painelEsquerdoVerde = new JPanel(new FlowLayout(FlowLayout.LEFT));
                painelEsquerdoVerde.setOpaque(false);
                JLabel iconeVinculoLabel = new JLabel(iconeVinculoEmpresa);
                JLabel nomeEmpresaLabel = new JLabel(empresa.nomeEmpresa());
                nomeEmpresaLabel.setForeground(Color.DARK_GRAY);
                nomeEmpresaLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
                painelEsquerdoVerde.add(iconeVinculoLabel);
                painelEsquerdoVerde.add(nomeEmpresaLabel);

                blocoVerde.add(painelEsquerdoVerde, BorderLayout.WEST);
                blocoVerde.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            }

            if (pessoa.hospedado()) {
                BotaoComSombra botaoHospedado = Botoes.btn_cinza("Hospedado");
                blocoVerde.add(botaoHospedado, BorderLayout.EAST);
            }

            painelDireita.add(blocoVermelho);
            painelDireita.add(blocoVerde);

            blocoPessoaButton.add(blocoAzul, BorderLayout.WEST);
            blocoPessoaButton.add(painelDireita, BorderLayout.CENTER);

            var corPessoaButton = new Color(0xE2E2E2);

            blocoPessoaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    blocoPessoaButton.setBackground(corPessoaButton);
                    painelDireita.setBackground(corPessoaButton);
                    blocoAzul.setBackground(corPessoaButton);
                    blocoVermelho.setBackground(corPessoaButton);
                    blocoVerde.setBackground(corPessoaButton);
                    labelFotoPessoa.setBackground(corPessoaButton);
                    ((BotaoArredondado) e.getSource()).setShowBorder(true, corPessoaButton);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    blocoPessoaButton.setBackground(Color.WHITE);
                    painelDireita.setBackground(Color.WHITE);
                    blocoAzul.setBackground(Color.WHITE);
                    blocoVerde.setBackground(Color.WHITE);
                    blocoVermelho.setBackground(Color.WHITE);
                    labelFotoPessoa.setBackground(Color.WHITE);
                    ((BotaoArredondado) e.getSource()).setShowBorder(false, Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    new IdentificacaoPessoaFrame(pessoa.cpf(), true);
                }
            });

            gbc.gridy = i + 1;
            leftPanel.add(blocoPessoaButton, gbc);
        }
    }

    private void addHeaderToLeftPanel(PanelArredondado leftPanel) {
        JLabel headerLabel = new JLabel("Header for Left Panel", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        headerLabel.setOpaque(true);
        headerLabel.setBackground(Color.GREEN);
        headerLabel.setForeground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        leftPanel.add(headerLabel, gbc);
    }


    @Override
    public void refreshPanel() {
        removeAll();
        initializePanel();
        revalidate();
        repaint();
    }

}