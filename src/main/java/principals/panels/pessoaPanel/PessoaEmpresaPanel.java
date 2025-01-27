package principals.panels.pessoaPanel;

import buttons.BotaoComSombra;
import principals.panels.empresaPanels.IdentificacaoEmpresaFrame;
import principals.tools.*;
import repository.EmpresaRepository;
import repository.PessoaRepository;
import response.PessoaResponse;
import textField.TextFieldComSobra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static buttons.Botoes.*;
import static principals.tools.Icones.*;
import static principals.tools.ImagemArredodanda.arredondar;
import static principals.tools.ImagemArredodanda.convertImageIconToBufferedImage;
import static principals.tools.Mascaras.adicionarMascaraCPF;
import static principals.tools.Resize.resizeIcon;

public class PessoaEmpresaPanel extends JPanel implements Refreshable {
    private static final Integer FEMININO = 1;

    private final TextFieldComSobra textFieldBuscaPorNome = new TextFieldComSobra();
    private final TextFieldComSobra textFieldBuscaPorCPF = new TextFieldComSobra();
    private final JPanel bottomPanel = new JPanel();

    private final PessoaRepository pessoaRepository;

    public PessoaEmpresaPanel() {
        pessoaRepository = new PessoaRepository();
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;

        PanelArredondado leftPanel = new PanelArredondado();
        leftPanel.setBackground(CorPersonalizada.LIGHT_GRAY);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder());

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(leftPanel.getWidth(), 150));
        topPanel.setBackground(CorPersonalizada.LIGHT_GRAY);
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder());

        bottomPanel.setLayout(new GridBagLayout());
        bottomPanel.setBackground(CorPersonalizada.LIGHT_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder());

        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(bottomPanel, BorderLayout.CENTER);

        configureTopPanel(topPanel);
        atualizarPainelDePessoas(pessoaRepository.buscarTodasAsPessoasComPaginacao(0));

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(leftScrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void configureTopPanel(JPanel topPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 4);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        BotaoComSombra btnAdicionarPessoa = btn_verde(" Identificação de Pessoa ");
        btnAdicionarPessoa.setPreferredSize(new Dimension(170, 40));

        BotaoComSombra btnAdicionarEmpresa = btn_azul("Identificação de Empresa");
        btnAdicionarEmpresa.setPreferredSize(new Dimension(170, 40));

        BotaoComSombra btnHospedados = btn_cinza(" Hospedados:  " + pessoaRepository.qutPessoasHospedadas());
        btnHospedados.setPreferredSize(new Dimension(130, 40));

        BotaoComSombra btnReset = btn_branco("Resetar");
        btnHospedados.setPreferredSize(new Dimension(120, 40));

        btnAdicionarPessoa.addActionListener(evt -> {
            var identificacaoPessoaFrame = new IdentificacaoPessoaFrame(null, false);
            identificacaoPessoaFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshPanel();
                }
            });
        });

        btnAdicionarEmpresa.addActionListener(evt -> {
            var identificacaoEmpresaFrame = new IdentificacaoEmpresaFrame();
            identificacaoEmpresaFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshPanel();
                }
            });
        });

        btnHospedados.addActionListener(evt -> atualizarPainelDePessoas(pessoaRepository.buscarPessoasHospedadas()));
        btnReset.addActionListener(evt -> atualizarPainelDePessoas(pessoaRepository.buscarTodasAsPessoasComPaginacao(0)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnAdicionarEmpresa);
        buttonPanel.add(btnAdicionarPessoa);
        buttonPanel.add(btnHospedados);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        topPanel.add(buttonPanel, gbc);

        textFieldBuscaPorNome.setPreferredSize(new Dimension(300, 40));
        textFieldBuscaPorNome.setPlaceholder("Buscar por nome: ");

        textFieldBuscaPorCPF.setPreferredSize(new Dimension(150, 40));
        textFieldBuscaPorCPF.setPlaceholder("Buscar por CPF: ");
        adicionarMascaraCPF(textFieldBuscaPorCPF);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(textFieldBuscaPorNome, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        topPanel.add(textFieldBuscaPorCPF, gbc);

        configureSearchBehavior();
    }

    private void configureSearchBehavior() {
        textFieldBuscaPorNome.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            private void atualizarBusca() {
                String texto = textFieldBuscaPorNome.getText().trim();
                if (texto.length() >= 3) {
                    atualizarPainelDePessoas(pessoaRepository.buscarPessoaPorNome(texto));
                }
            }
        });

        textFieldBuscaPorCPF.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                atualizarBusca();
            }

            private void atualizarBusca() {
                String texto = textFieldBuscaPorCPF.getText().trim();
                 if (texto.length() == 14) {
                    atualizarPainelDePessoas(List.of(pessoaRepository.buscarPessoaPorCPF(texto)));
                }
            }
        });
    }

    private void atualizarPainelDePessoas(List<PessoaResponse> pessoas) {
        bottomPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (PessoaResponse pessoa : pessoas) {
            BotaoArredondado blocoPessoaButton = adicionarBlocoPessoa(pessoa);
            bottomPanel.add(blocoPessoaButton, gbc);
            gbc.gridy++;
        }

        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    public BotaoArredondado adicionarBlocoPessoa(PessoaResponse pessoa) {
        EmpresaRepository empresaRepository = new EmpresaRepository(pessoaRepository);

        var iconePessoa = resizeIcon(usuarios, 20, 20);
        var iconeVinculoEmpresa = resizeIcon(linked, 20, 20);
        var empresa = empresaRepository.buscarUltimaEmpresaCadastradaPorCpfPessoa(pessoa.cpf());

        BufferedImage pessoaFoto = null;

        try {
            pessoaFoto = pessoaRepository.buscarFotoBufferedPessoaPorId(pessoa.id());
        } catch (SQLException | IOException ignored) {}

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

        LabelArredondado labelFotoPessoa = new LabelArredondado("");
        labelFotoPessoa.setBackground(Color.WHITE);

        int largura = 90;
        int altura = 90;

        ImageIcon icon;
        if (pessoaFoto != null) {
            var imagemArredondada = arredondar(pessoaFoto);
            icon = resizeIcon(new ImageIcon(imagemArredondada), largura, altura);
        } else if (pessoa.sexo().equals(FEMININO)) {
            var imagemArredondada = arredondar(convertImageIconToBufferedImage(user_sem_foto_feminino));
            icon = resizeIcon(new ImageIcon(imagemArredondada), largura, altura);
        } else {
            var imagemArredondada = arredondar(convertImageIconToBufferedImage(user_sem_foto));
            icon = resizeIcon(new ImageIcon(imagemArredondada), largura, altura);
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
            BotaoComSombra botaoEmpresa = btn_branco(empresa.nomeEmpresa());
            botaoEmpresa.setIcon(iconeVinculoEmpresa);
            blocoVerde.add(botaoEmpresa, BorderLayout.WEST);
        }

        if (pessoa.hospedado()) {
            BotaoComSombra botaoHospedado = btn_cinza("Hospedado");
            blocoVerde.add(botaoHospedado, BorderLayout.EAST);
        }

        painelDireita.add(blocoVermelho);
        painelDireita.add(blocoVerde);

        blocoPessoaButton.add(blocoAzul, BorderLayout.WEST);
        blocoPessoaButton.add(painelDireita, BorderLayout.CENTER);

        var corPessoaButton = new Color(0xE2E2E2);

        blocoPessoaButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                blocoPessoaButton.setBackground(corPessoaButton);
                painelDireita.setBackground(corPessoaButton);
                blocoAzul.setBackground(corPessoaButton);
                blocoVermelho.setBackground(corPessoaButton);
                blocoVerde.setBackground(corPessoaButton);
                labelFotoPessoa.setBackground(corPessoaButton);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                blocoPessoaButton.setBackground(Color.WHITE);
                painelDireita.setBackground(Color.WHITE);
                blocoAzul.setBackground(Color.WHITE);
                blocoVermelho.setBackground(Color.WHITE);
                blocoVerde.setBackground(Color.WHITE);
                labelFotoPessoa.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new IdentificacaoPessoaFrame(pessoa.cpf(), true);
            }
        });

        return blocoPessoaButton;
    }


    @Override
    public void refreshPanel() {
        atualizarPainelDePessoas(pessoaRepository.buscarTodasAsPessoasComPaginacao(0));
    }
}
