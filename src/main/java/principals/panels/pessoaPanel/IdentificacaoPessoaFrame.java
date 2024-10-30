package principals.panels.pessoaPanel;

import principals.tools.Cor;
import principals.tools.JComboBoxArredondado;
import principals.tools.JTextFieldComTextoFixoArredondado;
import repository.LocalizacaoRepository;
import response.Objeto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class IdentificacaoPessoaFrame extends JFrame {
    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();

    private final JTextFieldComTextoFixoArredondado campoNome;
    private final JTextFieldComTextoFixoArredondado campoCPF;
    private final JTextFieldComTextoFixoArredondado campoRG;
    private final JTextFieldComTextoFixoArredondado campoTelefone;
    private final JTextFieldComTextoFixoArredondado campoEmail;
    private final JTextFieldComTextoFixoArredondado campoDataNascimento;
    private final JTextFieldComTextoFixoArredondado campoEndereco;
    private final JTextFieldComTextoFixoArredondado campoNumero;
    private final JTextFieldComTextoFixoArredondado campoComplemento;

    private final JComboBoxArredondado<String> paisComboBox;
    private final JComboBoxArredondado<String> estadoComboBox;
    private final JComboBoxArredondado<String> municipioComboBox;

    // Campos de radio para "Está Hospedado?" e "Cliente Novo?"
    private final JRadioButton hospedadoSim;
    private final JRadioButton hospedadoNao;
    private final JRadioButton clienteNovoSim;
    private final JRadioButton clienteNovoNao;

    public IdentificacaoPessoaFrame() {
        setTitle("Identificação de Pessoa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 550);
        setPreferredSize(new Dimension(700, 500));
        setMinimumSize(new Dimension(700, 500));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(700, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
        JLabel titulo = new JLabel("Identificação de Pessoa");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel camposPanel = new JPanel();
        camposPanel.setBackground(Color.WHITE);

        Dimension fieldDimension = new Dimension(200, 25);

        campoNome = new JTextFieldComTextoFixoArredondado("Nome: ", 30);
        campoNome.setPreferredSize(fieldDimension);

        campoCPF = new JTextFieldComTextoFixoArredondado("CPF: ", 10);
        campoCPF.setPreferredSize(fieldDimension);
        adicionarMascaraCPF(campoCPF);

        campoRG = new JTextFieldComTextoFixoArredondado("RG: ", 10);
        campoRG.setPreferredSize(fieldDimension);
        adicionarMascaraRG(campoRG);

        campoTelefone = new JTextFieldComTextoFixoArredondado("Fone: ", 15);
        campoTelefone.setPreferredSize(fieldDimension);
        adicionarMascaraTelefone(campoTelefone);

        campoEmail = new JTextFieldComTextoFixoArredondado("Email: ", 20);
        campoEmail.setPreferredSize(fieldDimension);

        campoDataNascimento = new JTextFieldComTextoFixoArredondado("Nascimento: ", 10);
        campoDataNascimento.setPreferredSize(fieldDimension);
        adicionarMascaraDataNascimento(campoDataNascimento);

        campoEndereco = new JTextFieldComTextoFixoArredondado("Endereco: ", 20);
        campoEndereco.setPreferredSize(fieldDimension);

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 5);
        campoNumero.setPreferredSize(fieldDimension);

        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 25);
        campoComplemento.setPreferredSize(fieldDimension);

        paisComboBox = new JComboBoxArredondado<>();
        estadoComboBox = new JComboBoxArredondado<>();
        municipioComboBox = new JComboBoxArredondado<>();

        paisComboBox.setEditable(true);
        paisComboBox.setPreferredSize(new Dimension(190, 30));
        estadoComboBox.setEditable(true);
        estadoComboBox.setPreferredSize(new Dimension(190, 30));
        municipioComboBox.setEditable(true);
        municipioComboBox.setPreferredSize(new Dimension(190, 30));

        try {
            List<Objeto> paises = localizacaoRepository.buscarPaises();
            List<String> paisDescricoes = paises.stream().map(Objeto::descricao).toList();
            paisDescricoes.forEach(paisComboBox::addItem);

            Objeto brasil = localizacaoRepository.buscaPaisPorNome("Brasil");
            if (brasil != null) {
                paisComboBox.setSelectedItem(brasil.descricao());
                carregarEstados(brasil.id());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        paisComboBox.addActionListener(e -> {
            String paisSelecionado = (String) paisComboBox.getSelectedItem();
            if (paisSelecionado != null) {
                try {
                    Objeto pais = localizacaoRepository.buscaPaisPorNome(paisSelecionado);
                    if (pais != null) carregarEstados(pais.id());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        estadoComboBox.addActionListener(e -> {
            String estadoSelecionado = (String) estadoComboBox.getSelectedItem();
            if (estadoSelecionado != null) {
                try {
                    Objeto estado = localizacaoRepository.buscaEstadoPorNomeEId(estadoSelecionado, getPaisIdByName((String) paisComboBox.getSelectedItem()));
                    if (estado != null) carregarMunicipios(estado.id());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        hospedadoSim = new JRadioButton("Sim");
        hospedadoSim.setBackground(Color.WHITE);
        hospedadoSim.setBorderPainted(false);
        hospedadoSim.setFocusPainted(false);

        hospedadoNao = new JRadioButton("Não");
        hospedadoNao.setBackground(Color.WHITE);
        hospedadoNao.setBorderPainted(false);
        hospedadoNao.setFocusPainted(false);


        clienteNovoSim = new JRadioButton("Sim");
        clienteNovoNao = new JRadioButton("Não");

        ButtonGroup hospedadoGroup = new ButtonGroup();
        hospedadoGroup.add(hospedadoSim);
        hospedadoGroup.add(hospedadoNao);

        ButtonGroup clienteNovoGroup = new ButtonGroup();
        clienteNovoGroup.add(clienteNovoSim);
        clienteNovoGroup.add(clienteNovoNao);

        JPanel hospedadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hospedadoPanel.add(new JLabel("Está Hospedado?"));
        hospedadoPanel.setBackground(Color.WHITE);
        hospedadoPanel.setForeground(Cor.CINZA_CLARO);
        hospedadoPanel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hospedadoPanel.add(hospedadoSim);
        hospedadoPanel.add(hospedadoNao);

        JPanel clienteNovoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clienteNovoPanel.add(new JLabel("Cliente Novo?"));
        clienteNovoPanel.add(clienteNovoSim);
        clienteNovoPanel.add(clienteNovoNao);

        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        JTextFieldComTextoFixoArredondado[] campos = {campoNome, campoCPF, campoRG, campoTelefone, campoEmail, campoDataNascimento, campoEndereco, campoNumero, campoComplemento};
        for (JTextFieldComTextoFixoArredondado campo : campos) {
            campo.setFont(font);
            campo.setForeground(Color.DARK_GRAY);
        }

        GroupLayout layout = new GroupLayout(camposPanel);
        camposPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(campoNome)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoCPF)
                                .addComponent(campoRG))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoTelefone)
                                .addComponent(campoDataNascimento))
                        .addComponent(campoEmail)
                        .addGap(70)
                        .addComponent(campoEndereco)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoComplemento)
                                .addComponent(campoNumero))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(paisComboBox)
                                .addComponent(estadoComboBox)
                                .addComponent(municipioComboBox))
                        .addComponent(hospedadoPanel)
                        .addComponent(clienteNovoPanel)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(campoNome)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoCPF)
                        .addComponent(campoRG))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoTelefone)
                        .addComponent(campoDataNascimento))
                .addComponent(campoEmail)
                .addGap(70)
                .addComponent(campoEndereco)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoComplemento)
                        .addComponent(campoNumero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(paisComboBox)
                        .addComponent(estadoComboBox)
                        .addComponent(municipioComboBox))
                .addComponent(hospedadoPanel)
                .addComponent(clienteNovoPanel)
        );

        add(camposPanel, BorderLayout.CENTER);

        JPanel botaoPanel = new JPanel();
        botaoPanel.setPreferredSize(new Dimension(700, 50));
        botaoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botaoPanel.add(btnSalvar);

        add(botaoPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void carregarEstados(long paisId) {
        estadoComboBox.removeAllItems();
        try {
            List<Objeto> estados = localizacaoRepository.buscarEstadosPorPaisId(paisId);
            List<String> estadoDescricoes = estados.stream().map(Objeto::descricao).toList();
            estadoDescricoes.forEach(estadoComboBox::addItem);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void carregarMunicipios(long estadoId) {
        municipioComboBox.removeAllItems();
        try {
            List<Objeto> municipios = localizacaoRepository.buscarMunicipiosPorEstadoId(estadoId);
            List<String> municipioDescricoes = municipios.stream().map(Objeto::descricao).toList();
            municipioDescricoes.forEach(municipioComboBox::addItem);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private long getPaisIdByName(String paisNome) throws SQLException {
        Objeto pais = localizacaoRepository.buscaPaisPorNome(paisNome);
        return pais != null ? pais.id() : 0L;
    }



    private void adicionarMascaraDataNascimento(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 8) {
                    texto = texto.substring(0, 8);
                }

                StringBuilder formatado = new StringBuilder("Nascimento: ");
                if (texto.length() >= 2) {
                    formatado.append(texto, 0, 2).append("/");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 4) {
                    formatado.append(texto, 2, 4).append("/").append(texto.substring(4));
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }

                campo.setText(formatado.toString());
            }
        });
    }


    private void adicionarMascaraTelefone(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder("Fone: ");
                if (texto.length() >= 2) {
                    formatado.append("(").append(texto, 0, 2).append(") ");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 7) {
                    formatado.append(texto, 2, 7).append("-").append(texto.substring(7));
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }

                campo.setText(formatado.toString());
            }
        });
    }


    private void adicionarMascaraCPF(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder("CPF: ");
                if (texto.length() > 3) {
                    formatado.append(texto, 0, 3).append(".");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 6) {
                    formatado.append(texto, 3, 6).append(".");
                } else if (texto.length() > 3) {
                    formatado.append(texto.substring(3));
                }
                if (texto.length() > 9) {
                    formatado.append(texto, 6, 9).append("-");
                } else if (texto.length() > 6) {
                    formatado.append(texto.substring(6));
                }
                if (texto.length() > 9) {
                    formatado.append(texto.substring(9));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    private void adicionarMascaraRG(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");


                if (texto.length() > 13) {
                    texto = texto.substring(0, 13);
                }

                StringBuilder formatado = new StringBuilder("RG: ");
                if (texto.length() > 2) {
                    formatado.append(texto, 0, 2).append(".");
                    if (texto.length() > 5) {
                        formatado.append(texto, 2, 5).append(".");
                        if (texto.length() > 8) {
                            formatado.append(texto, 5, 8).append(".");
                            if (texto.length() > 12) {
                                formatado.append(texto, 8, 12).append("-");
                            } else {
                                formatado.append(texto.substring(8));
                            }
                        } else {
                            formatado.append(texto.substring(5));
                        }
                    } else {
                        formatado.append(texto.substring(2));
                    }
                } else {
                    formatado.append(texto);
                }

                campo.setText(formatado.toString());
                campo.setCaretPosition(campo.getText().length());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoPessoaFrame::new);
    }
}
