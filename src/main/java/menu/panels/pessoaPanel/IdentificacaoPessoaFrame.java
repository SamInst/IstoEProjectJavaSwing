package menu.panels.pessoaPanel;

import enums.GeneroEnum;
import lombok.SneakyThrows;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.PessoaRequest;
import response.CepInfo;
import response.Objeto;
import response.PessoaResponse;
import tools.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static buttons.Botoes.*;
import static tools.EscurecerImagemDemo.escurecerImagem;
import static tools.Icones.user_sem_foto;
import static tools.Mascaras.*;
import static tools.Resize.resizeIcon;

public class IdentificacaoPessoaFrame extends JFrame {

    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();
    private final ViaCepService viaCepService = new ViaCepService();

    JTextFieldComTextoFixoArredondado campoNomePessoa;
    JTextFieldComTextoFixoArredondado campoCPF;
    JTextFieldComTextoFixoArredondado campoRG;
    JTextFieldComTextoFixoArredondado campoIdade;
    JTextFieldComTextoFixoArredondado campoTelefone;
    JTextFieldComTextoFixoArredondado campoEmail;
    JTextFieldComTextoFixoArredondado campoDataNascimento;
    JTextFieldComTextoFixoArredondado campoEndereco;
    JTextFieldComTextoFixoArredondado campoNumero;
    JTextFieldComTextoFixoArredondado campoComplemento;
    JTextFieldComTextoFixoArredondado campoCEP;
    JTextFieldComTextoFixoArredondado campoBairro;

    private final JComboBoxArredondado<String> paisComboBox;
    private final JComboBoxArredondado<String> estadoComboBox;
    private final JComboBoxArredondado<String> municipioComboBox;
    private final JComboBoxArredondado<String> genero;

    JLabel fotoLabel = new JLabel();
    JRadioButton hospedadoSim;
    JRadioButton hospedadoNao;
    JRadioButton clienteNovoSim;
    JRadioButton clienteNovoNao;
    JPanel statusPanel;
    JLabel statusLabel;
    String foto_usuario_path = "";
    JLabel textoLabel = new JLabel("Alterar imagem");

    PanelArredondado quantidadeHospedagemPanel = new PanelArredondado();
    JLabel quantidadeHospedagemLabel = new JLabel("0");

    Font font = new Font("Roboto", Font.PLAIN, 16);
    final int[] generoSelecionado = new int[1];
    int idadeCalculada;

    int largura_foto = 180;
    int altura_foto = 211;

    String cpfExterno;

    ImageIcon user_sem_foto_masculino = resizeIcon(user_sem_foto, largura_foto, altura_foto);
    ImageIcon user_sem_foto_feminino = resizeIcon(Icones.user_sem_foto_feminino, largura_foto, altura_foto);

    AtomicReference<ImageIcon> foto_usuario = new AtomicReference<>(user_sem_foto_masculino);


    public IdentificacaoPessoaFrame(String cpf, boolean viewOnly) {
        cpfExterno = cpf;
        setTitle("Identificação de Pessoa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 440);
        setPreferredSize(new Dimension(900, 440));
        setMinimumSize(new Dimension(900, 440));
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(700, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));

        JLabel titulo = new JLabel("Identificação de Pessoa");
        titulo.setFont(new Font("Roboto", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel camposPanel = new JPanel();
        camposPanel.setBackground(Color.WHITE);

        Dimension fieldDimension = new Dimension(200, 25);

        campoNomePessoa = new JTextFieldComTextoFixoArredondado("* Nome: ", 30);
        campoNomePessoa.setPreferredSize(fieldDimension);

        campoCPF = new JTextFieldComTextoFixoArredondado("* CPF: ", 10);
        campoCPF.setPreferredSize(fieldDimension);
        adicionarMascaraCPF(campoCPF, cpfExterno);

        campoRG = new JTextFieldComTextoFixoArredondado("RG: ", 10);
        campoRG.setPreferredSize(fieldDimension);
        adicionarMascaraRG(campoRG);

        campoIdade = new JTextFieldComTextoFixoArredondado("Idade: ", 10);
        campoIdade.setPreferredSize(fieldDimension);
        campoIdade.setEditable(false);

        campoTelefone = new JTextFieldComTextoFixoArredondado("* Fone: ", 15);
        campoTelefone.setPreferredSize(fieldDimension);
        adicionarMascaraTelefone(campoTelefone);

        campoEmail = new JTextFieldComTextoFixoArredondado("Email: ", 20);
        campoEmail.setPreferredSize(fieldDimension);

        campoDataNascimento = new JTextFieldComTextoFixoArredondado("Nascimento: ", 10);
        campoDataNascimento.setPreferredSize(fieldDimension);
        adicionarMascaraData(campoDataNascimento);

        campoEndereco = new JTextFieldComTextoFixoArredondado("Endereço: ", 35);

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 5);
        campoNumero.setPreferredSize(fieldDimension);

        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 20);
        campoComplemento.setPreferredSize(fieldDimension);

        campoBairro = new JTextFieldComTextoFixoArredondado("Bairro: ", 10);

        campoCEP = new JTextFieldComTextoFixoArredondado("* CEP: ", 4);
        campoCEP.setFont(font);
        adicionarMascaraCEP(campoCEP);

        paisComboBox = new JComboBoxArredondado<>();
        estadoComboBox = new JComboBoxArredondado<>();
        municipioComboBox = new JComboBoxArredondado<>();
        genero = new JComboBoxArredondado<>();

        paisComboBox.setEditable(true);
        paisComboBox.setPreferredSize(new Dimension(190, 30));

        estadoComboBox.setEditable(true);
        estadoComboBox.setPreferredSize(new Dimension(190, 30));

        municipioComboBox.setEditable(true);
        municipioComboBox.setPreferredSize(new Dimension(190, 30));

        genero.setEditable(true);
        genero.setPreferredSize(new Dimension(140, 30));

        for (GeneroEnum generoEnum : GeneroEnum.values()) {
            genero.addItem(generoEnum.name());
        }


        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBackground(Color.LIGHT_GRAY);
        painelEsquerdo.setPreferredSize(new Dimension(220, 550));
        add(painelEsquerdo, BorderLayout.WEST);

        JPanel fotoPanel = new JPanel();
        fotoPanel.setBackground(Color.LIGHT_GRAY);
        fotoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fotoPanel.setPreferredSize(new Dimension(largura_foto, altura_foto));
        fotoPanel.setMaximumSize(new Dimension(largura_foto, altura_foto));
        fotoPanel.setLayout(new OverlayLayout(fotoPanel));

        textoLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        textoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textoLabel.setVerticalAlignment(SwingConstants.CENTER);
        textoLabel.setFont(font);
        textoLabel.setForeground(new Color(255, 255, 255, 200));
        textoLabel.setVisible(false);
        fotoPanel.add(textoLabel);

        fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fotoLabel.setVerticalAlignment(SwingConstants.CENTER);
        fotoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fotoLabel.setIcon(foto_usuario.get());

        fotoPanel.add(fotoLabel, BorderLayout.CENTER);
        painelEsquerdo.add(fotoPanel, BorderLayout.CENTER);

        fotoLabel.addMouseListener(mouseAdapter());

        genero.addActionListener(e -> generoActionListener());

        fotoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\IstoEPousada\\Usuarios\\Fotos"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                FileNameExtensionFilter imageFilter =
                        new FileNameExtensionFilter("Imagens (JPG, JPEG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(imageFilter);

                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    foto_usuario_path = selectedFile.getAbsolutePath();
                    try {
                        ImageIcon selectedImage = new ImageIcon(selectedFile.getAbsolutePath());
                        foto_usuario.set(resizeIcon(selectedImage, largura_foto, altura_foto));
                        fotoLabel.setIcon(foto_usuario.get());

                        for (ActionListener listener : genero.getActionListeners()) {
                            genero.removeActionListener(listener);
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao carregar a imagem." + ex, "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        quantidadeHospedagemPanel.setBackground(Color.RED);
        quantidadeHospedagemPanel.setBounds(135, 4, 40, 40);
        quantidadeHospedagemPanel.setToolTipText("Quantidade de Hospedagens");
        quantidadeHospedagemPanel.setVisible(false);

        quantidadeHospedagemLabel.setForeground(Color.WHITE);
        quantidadeHospedagemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quantidadeHospedagemLabel.setVerticalAlignment(SwingConstants.CENTER);
        quantidadeHospedagemLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        quantidadeHospedagemPanel.add(quantidadeHospedagemLabel);

        fotoLabel.add(quantidadeHospedagemPanel);

        statusPanel = new JPanel();
        statusPanel.setBackground(Color.DARK_GRAY);
        statusPanel.setPreferredSize(new Dimension(200, 30));
        statusPanel.setMaximumSize(new Dimension(250, 30));
        statusLabel = new JLabel("Situação");
        statusLabel.setFont(font);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);
        painelEsquerdo.add(statusPanel);

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

        campoDataNascimento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String dataNascimentoTexto = campoDataNascimento.getText()
                        .replace("Nascimento: ", "")
                        .trim();

                if (dataNascimentoTexto.length() == 10) {
                    try {
                        LocalDate dataNascimento = LocalDate.parse(dataNascimentoTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        idadeCalculada = Period.between(dataNascimento, LocalDate.now()).getYears();
                        campoIdade.setText("Idade: " + idadeCalculada + " ANOS");
                    } catch (Exception ex) {
                        campoIdade.setText("Idade: ");
                    }
                } else {
                    campoIdade.setText("Idade: ");
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
        clienteNovoSim.setBackground(Color.WHITE);
        clienteNovoSim.setBorderPainted(false);
        clienteNovoSim.setFocusPainted(false);

        clienteNovoNao = new JRadioButton("Não");
        clienteNovoNao.setBackground(Color.WHITE);
        clienteNovoNao.setBorderPainted(false);
        clienteNovoNao.setFocusPainted(false);

        ButtonGroup hospedadoGroup = new ButtonGroup();
        hospedadoGroup.add(hospedadoSim);
        hospedadoGroup.add(hospedadoNao);

        ButtonGroup clienteNovoGroup = new ButtonGroup();
        clienteNovoGroup.add(clienteNovoSim);
        clienteNovoGroup.add(clienteNovoNao);

        JPanel clienteNovoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clienteNovoPanel.setPreferredSize(new Dimension(200, 30));
        clienteNovoPanel.setMaximumSize(new Dimension(250, 30));
        clienteNovoPanel.add(new JLabel("Cliente Novo?"));
        clienteNovoPanel.setBackground(Color.WHITE);
        clienteNovoPanel.setForeground(CorPersonalizada.LIGHT_GRAY);
        clienteNovoPanel.setFont(font);
        clienteNovoPanel.add(clienteNovoSim);
        clienteNovoPanel.add(clienteNovoNao);

        painelEsquerdo.add(clienteNovoPanel);

        JPanel hospedadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hospedadoPanel.setPreferredSize(new Dimension(200, 30));
        hospedadoPanel.setMaximumSize(new Dimension(250, 30));
        hospedadoPanel.add(new JLabel("Está Hospedado?"));
        hospedadoPanel.setBackground(Color.WHITE);
        hospedadoPanel.setForeground(CorPersonalizada.LIGHT_GRAY);
        hospedadoPanel.setFont(font);
        hospedadoPanel.add(hospedadoSim);
        hospedadoPanel.add(hospedadoNao);

        painelEsquerdo.add(hospedadoPanel);

        JTextFieldComTextoFixoArredondado[] campos = {campoNomePessoa, campoCPF, campoRG, campoTelefone, campoEmail, campoDataNascimento, campoEndereco, campoNumero, campoComplemento, campoIdade, campoBairro};
        for (JTextFieldComTextoFixoArredondado campo : campos) {
            campo.setFont(font);
            campo.setForeground(Color.DARK_GRAY);
        }

        GroupLayout layout = new GroupLayout(camposPanel);
        camposPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoNomePessoa)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(genero, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoCPF)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoRG)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoDataNascimento))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoTelefone)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoIdade))
                        .addComponent(campoEmail)
                        .addGap(40)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoEndereco)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoCEP, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoComplemento)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoBairro)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoNumero))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(paisComboBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(estadoComboBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(municipioComboBox))
                        .addGap(10)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoNomePessoa)
                        .addComponent(genero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoCPF)
                        .addComponent(campoRG)
                        .addComponent(campoDataNascimento))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoTelefone)
                        .addComponent(campoIdade))
                .addComponent(campoEmail)
                .addGap(40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoEndereco)
                        .addComponent(campoCEP))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoComplemento)
                        .addComponent(campoBairro)
                        .addComponent(campoNumero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(paisComboBox)
                        .addComponent(estadoComboBox)
                        .addComponent(municipioComboBox))
                .addGap(10)
        );
        add(camposPanel, BorderLayout.CENTER);

        JPanel botaoPanel = new JPanel();
        botaoPanel.setPreferredSize(new Dimension(700, 50));
        botaoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        campoCPF.addKeyListener(new KeyAdapter() {
            @SneakyThrows
            @Override
            public void keyReleased(KeyEvent e) {
                String cpf = cpfExterno != null ? cpfExterno : campoCPF.getText()
                        .replace("* CPF:", "").trim();

                if (cpf.length() == 14) {
                    verificarSituacao(cpf);
                    preencherCamposPessoa(cpf);
                }
            }
        });

        campoCEP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String cep = campoCEP.getText()
                        .replaceFirst("\\* CEP:", "")
                        .trim()
                        .replace("-", "");

                if (cep.length() == 8) {
                    preencherEnderecoComCep(cep);
                }
            }
        });

        JButton btnLimpar = btn_branco("Limpar Campos");
        JButton btnSalvar = btn_verde("Salvar Dados");
        JButton btnEditar = btn_cinza("Editar Dados");

        botaoPanel.add(btnSalvar);
        botaoPanel.add(btnLimpar);
        botaoPanel.add(btnEditar);
        btnEditar.setVisible(false);

        btnSalvar.addActionListener(a -> {
            try {
                adicionarPessoa();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        btnLimpar.addActionListener(a -> limparCampos());

        add(botaoPanel, BorderLayout.SOUTH);
        setVisible(true);

        if (cpfExterno != null && !cpfExterno.isBlank()) {
          
            
            try {
                campoCPF.setText("* CPF: " + cpfExterno);
                if (verificarSituacao(cpfExterno)){
                    preencherCamposPessoa(cpfExterno);
                    if (viewOnly){
                        btnSalvar.setVisible(false);
                        btnLimpar.setVisible(false);
                        btnEditar.setVisible(true);
                        desabilitarCamposParaPesquisa();
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao buscar dados da pessoa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }

        btnEditar.addActionListener(a -> {
                dispose();
                new IdentificacaoPessoaFrame(cpfExterno, false);
        });
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

    public void adicionarPessoa() throws SQLException {
        String nomePessoa = Normalizer.normalize(
                campoNomePessoa.getText()
                        .replace("* Nome:", "")
                        .trim()
                        .toUpperCase(),
                Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        String dataNascimento = campoDataNascimento.getText()
                .replace("Nascimento: ", "")
                .replace("null", "")
                .trim();

        LocalDate novaDataNascimento = !dataNascimento.isEmpty() ?
                LocalDate.parse(dataNascimento, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : null;

        String cpf = campoCPF.getText()
                .trim()
                .replaceFirst("\\* CPF: ", "");

        String rg = campoRG.getText()
                .trim()
                .replaceFirst("RG: ", "")
                .replaceAll("[^0-9]", "");

        String email = campoEmail.getText()
                .replace("Email:", "")
                .trim()
                .toUpperCase();

        String telefone = campoTelefone.getText()
                .trim()
                .replaceFirst("\\* Fone: ", "");

        String endereco = campoEndereco.getText()
                .trim()
                .replaceAll("(?i)^endereço:\\s*", "")
                .toUpperCase();

        String complemento = campoComplemento.getText()
                .replace("Complemento: ", "")
                .trim()
                .toUpperCase();

        String numero = campoNumero.getText()
                .replace("N*: ", "")
                .trim()
                .toUpperCase();

        Boolean hospedado = hospedadoSim.isSelected();

        Integer vezesHospedado = hospedado ? + 1 : 0;

        Boolean clienteNovo = clienteNovoSim.isSelected();

        String cep = campoCEP.getText()
                .replaceFirst("\\* CEP:", "")
                .trim();

        String bairro = campoBairro.getText()
                .replace("Bairro:", "")
                .trim()
                .toUpperCase();

        String idade = campoIdade.getText()
                .replaceFirst("Idade: ", "")
                .replace("ANOS", "")
                .trim();

        Long pais = null;
        Long estado = null;
        Long municipio = null;

        try {
            pais = paisComboBox.getSelectedItem() != null ? localizacaoRepository.buscaPaisPorNome((String) paisComboBox.getSelectedItem()).id() : null;
            estado = estadoComboBox.getSelectedItem() != null ? localizacaoRepository.buscaEstadoPorNomeEId((String) estadoComboBox.getSelectedItem(), pais).id() : null;
            municipio = municipioComboBox.getSelectedItem() != null ? localizacaoRepository.buscaMunicipioPorNomeEId(municipioComboBox.getSelectedItem().toString(), estado).id() : null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Não foi possivel associar país, estado ou município. " + e, "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        generoSelecionado[0] = GeneroEnum.valueOf((String) genero.getSelectedItem()).ordinal();

        if (!CPFValidator.isCPFValid(cpf)) {
            JOptionPane.showMessageDialog(this, "CPF inválido", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nomePessoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome da Pessoa obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (telefone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Telefone para contato obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "CPF obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cep.isEmpty()) {
            JOptionPane.showMessageDialog(this, "CEP obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pais == null) {
            JOptionPane.showMessageDialog(this, "País obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (estado == null) {
            JOptionPane.showMessageDialog(this, "Estado obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (municipio == null) {
            JOptionPane.showMessageDialog(this, "Município obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!(clienteNovoSim.isSelected() ||  clienteNovoNao.isSelected())) {
            JOptionPane.showMessageDialog(this, "É necessário informar se a pessoa é um cliente novo", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!(hospedadoSim.isSelected() ||  hospedadoNao.isSelected())) {
            JOptionPane.showMessageDialog(this, "É necessário informar se a pessoa está hospedada", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PessoaRequest pessoaRequest = new PessoaRequest(
                foto_usuario_path,
                nomePessoa,
                novaDataNascimento,
                !idade.isEmpty() ? Integer.parseInt(idade) : 0,
                generoSelecionado[0],
                cpf,
                rg,
                email,
                telefone,
                pais,
                estado,
                municipio,
                cep,
                endereco,
                bairro,
                complemento,
                hospedado,
                vezesHospedado,
                clienteNovo,
                numero
        );

        try {
            PessoaResponse pessoaResponse = pessoaRepository.buscarPessoaPorCPF(cpf);

            if (pessoaRepository.cpfExists(cpf)) {
                String pathAntigo = "";
                var pessoaPathAtual = pessoaRepository.buscarPathFotoPessoaPorId(pessoaResponse.id());


                if (verificaDadosAlterados(pessoaRequest, pessoaResponse, pathAntigo, foto_usuario_path)) {
                    pessoaRepository.atualizarPessoa(pessoaResponse.id(), pessoaRequest);
                    JOptionPane.showMessageDialog(this, "Dados pessoa atualizados com sucesso!");


                    if (pessoaPathAtual == null){
                        pessoaRepository.adicionarFotoPessoa(pessoaResponse.id(), foto_usuario_path);
                    }
                    else {
                        if (foto_usuario_path.isEmpty()) foto_usuario_path = pathAntigo;

                        if (!pathAntigo.equals(foto_usuario_path)) {
                            pessoaRepository.atualizarPathFotoPessoaPorFotoId(pessoaPathAtual.id(), foto_usuario_path);
                            JOptionPane.showMessageDialog(this, "Foto atualizada com sucesso!");
                        }
                    }
                    foto_usuario_path = "";
                }
                 else {
                    JOptionPane.showMessageDialog(this, "Pessoa ja Cadastrada!", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                var pessoaID = pessoaRepository.adicionarPessoa(pessoaRequest);

                pessoaRepository.adicionarFotoPessoa(pessoaID, foto_usuario_path);
                verificarSituacao(cpf);
                JOptionPane.showMessageDialog(this, "Pessoa adicionada com sucesso!");
                foto_usuario_path = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao adicionar pessoa. Verifique os dados e tente novamente." + e);
        }
    }

    private boolean verificaCPF(String cpf) throws SQLException {
        return pessoaRepository.cpfExists(cpf);
    }

    private void preencherEnderecoComCep(String cep) {
        try {
            CepInfo cepInfo = viaCepService.buscarCep(cep);

            if (cepInfo != null) {
                SwingUtilities.invokeLater(() -> {
                    campoEndereco.setText("Endereço: " + cepInfo.logradouro() + ", " + cepInfo.unidade());
                    campoBairro.setText("Bairro: " + cepInfo.bairro());
                    campoNumero.setText("N*: " + cepInfo.complemento());
                    paisComboBox.setSelectedItem("Brasil");
                    Objeto estado;
                    try {
                        estado = localizacaoRepository.buscaEstadoPorNomeEId(
                                cepInfo.estado(),
                                localizacaoRepository.buscaPaisPorNome("Brasil").id()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    if (estado != null) {
                        estadoComboBox.setSelectedItem(estado.descricao());

                        Objeto municipio = null;
                        try {
                            municipio = localizacaoRepository.buscaMunicipioPorNomeEId(cepInfo.localidade(), estado.id());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        if (municipio != null) {
                            municipioComboBox.setSelectedItem(municipio.descricao());
                        } else {
                            JOptionPane.showMessageDialog(this, "Município '" + cepInfo.localidade() + "' não encontrado no banco de dados.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Estado '" + cepInfo.estado() + "' não encontrado no banco de dados.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "CEP não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar o CEP: Verifique a Conexão com a internet -> " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verificarSituacao(String cpf) throws SQLException {
        boolean cadastrado = pessoaRepository.cpfExists(cpf);
        if (cadastrado) {
            statusLabel.setText("Cadastrado");
            statusPanel.setBackground(CorPersonalizada.DARK_GREEN);
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setText("Não cadastrado");
            statusPanel.setBackground(CorPersonalizada.RED_2);
            statusLabel.setForeground(Color.WHITE);
        }
        return cadastrado;
    }

    private void sobrescreverCamposPessoa(
            String nomePessoa,
            String telefone,
            String email,
            String cep,
            String endereco,
            String complemento,
            String bairro,
            String numero,
            Boolean hospedado,
            Boolean clienteNovo,
            ImageIcon foto,
            Integer idade,
            Integer sexo,
            String dataNascimento,
            String rg) {
        campoNomePessoa.setText("* Nome: " + nomePessoa);
        campoTelefone.setText("* Fone: " + telefone);
        campoEmail.setText("Email: " + email);
        campoCEP.setText("* CEP: " + cep);
        campoEndereco.setText("Endereço: " + endereco);
        campoComplemento.setText("Complemento: " + complemento);
        campoBairro.setText("Bairro: " + bairro);
        campoNumero.setText("N*: " + numero);
        campoIdade.setText("Idade: " + idade + " ANOS");
        genero.setSelectedItem(GeneroEnum.fromCodigo(sexo).name());
        campoDataNascimento.setText("Nascimento: " + dataNascimento);
        campoRG.setText("RG: " + rg);

        if (hospedado) hospedadoSim.setSelected(true);
        else hospedadoNao.setSelected(true);

        if (clienteNovo) clienteNovoSim.setSelected(true);
        else clienteNovoNao.setSelected(true);

        if (foto != null) {
            for (ActionListener listener : genero.getActionListeners()) {
                genero.removeActionListener(listener);
            }

            foto_usuario.set(foto);
            fotoLabel.setIcon(foto);
        } else {
            switch (sexo) {
                case 0, 2 ->{
                    foto_usuario.set(user_sem_foto_masculino);
                    fotoLabel.setIcon(user_sem_foto_masculino);
                }
                case 1 -> {
                    foto_usuario.set(user_sem_foto_feminino);
                    fotoLabel.setIcon(user_sem_foto_feminino);
                }
            }
        }
    }

    private void preencherCamposPessoa(String cpf) {
        try {
            var pessoa = pessoaRepository.buscarPessoaPorCPF(cpf);

            if (pessoa != null) {
                var foto = pessoaRepository.buscarFotoPessoaPorId(pessoa.id());

                if (verificaCPF(cpf)) {
                    sobrescreverCamposPessoa(
                            pessoa.nome(),
                            pessoa.telefone(),
                            pessoa.email(),
                            pessoa.cep(),
                            pessoa.endereco(),
                            pessoa.complemento(),
                            pessoa.bairro(),
                            pessoa.numero(),
                            pessoa.hospedado(),
                            pessoa.clienteNovo(),
                            foto,
                            pessoa.idade(),
                            pessoa.sexo(),
                            pessoa.data_nascimento() != null ? pessoa.data_nascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")): null,
                            pessoa.rg()
                    );

                    var pais = localizacaoRepository.buscarPaisPorId(pessoa.pais().id());
                    var estado = localizacaoRepository.buscarEstadoPorId(pessoa.estado().id());
                    var municipio = localizacaoRepository.buscarMunicipioPorId(pessoa.municipio().id());

                    paisComboBox.setSelectedItem(pais.descricao());
                    estadoComboBox.setSelectedItem(estado.descricao());
                    municipioComboBox.setSelectedItem(municipio.descricao());

                    quantidadeHospedagemLabel.setText(pessoa.vezes_hospedado().toString());
                    quantidadeHospedagemPanel.setVisible(true);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao buscar informações da pessoa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }

    public boolean verificaDadosAlterados(PessoaRequest pessoaRequest, PessoaResponse pessoa, String pathAntigo, String pathNovo) {
        System.out.println("Foto path: " + pathAntigo + " : " + pathNovo);
        System.out.println("Nome: " + pessoaRequest.nome() + " : " + pessoa.nome());
        System.out.println("Data de Nascimento: " + pessoaRequest.dataNascimento() + " : " + pessoa.data_nascimento());
        System.out.println("Idade: " + pessoaRequest.idade() + " : " + pessoa.idade());
        System.out.println("Sexo: " + pessoaRequest.sexo() + " : " + pessoa.sexo());
        System.out.println("CPF: " + pessoaRequest.cpf() + " : " + pessoa.cpf());
        System.out.println("RG: " + pessoaRequest.rg() + " : " + pessoa.rg());
        System.out.println("Email: " + pessoaRequest.email() + " : " + pessoa.email());
        System.out.println("Telefone: " + pessoaRequest.telefone() + " : " + pessoa.telefone());
        System.out.println("País: " + pessoaRequest.pais() + " : " + pessoa.pais().id());
        System.out.println("Estado: " + pessoaRequest.estado() + " : " + pessoa.estado().id());
        System.out.println("Município: " + pessoaRequest.municipio() + " : " + pessoa.municipio().id());
        System.out.println("CEP: " + pessoaRequest.cep() + " : " + pessoa.cep());
        System.out.println("Endereço: " + pessoaRequest.endereco() + " : " + pessoa.endereco());
        System.out.println("Bairro: " + pessoaRequest.bairro() + " : " + pessoa.bairro());
        System.out.println("Complemento: " + pessoaRequest.complemento() + " : " + pessoa.complemento());
        System.out.println("Hospedado: " + pessoaRequest.hospedado() + " : " + pessoa.hospedado());
        System.out.println("Cliente Novo: " + pessoaRequest.clienteNovo() + " : " + pessoa.clienteNovo());
        System.out.println("Número: " + pessoaRequest.numero() + " : " + pessoa.numero());

        return !(
                Objects.equals(pathAntigo, pathNovo) &&
                        Objects.equals(pessoaRequest.nome(), pessoa.nome()) &&
                        Objects.equals(pessoaRequest.dataNascimento(), pessoa.data_nascimento()) &&
                        Objects.equals(pessoaRequest.idade(), pessoa.idade()) &&
                        Objects.equals(pessoaRequest.sexo(), pessoa.sexo()) &&
                        Objects.equals(pessoaRequest.cpf(), pessoa.cpf()) &&
                        Objects.equals(pessoaRequest.rg(), pessoa.rg()) &&
                        Objects.equals(pessoaRequest.email(), pessoa.email()) &&
                        Objects.equals(pessoaRequest.telefone(), pessoa.telefone()) &&
                        Objects.equals(pessoaRequest.pais(), pessoa.pais().id()) &&
                        Objects.equals(pessoaRequest.estado(), pessoa.estado().id()) &&
                        Objects.equals(pessoaRequest.municipio(), pessoa.municipio().id()) &&
                        Objects.equals(pessoaRequest.cep(), pessoa.cep()) &&
                        Objects.equals(pessoaRequest.endereco(), pessoa.endereco()) &&
                        Objects.equals(pessoaRequest.bairro(), pessoa.bairro()) &&
                        Objects.equals(pessoaRequest.complemento(), pessoa.complemento()) &&
                        Objects.equals(pessoaRequest.hospedado(), pessoa.hospedado()) &&
                        Objects.equals(pessoaRequest.clienteNovo(), pessoa.clienteNovo()) &&
                        Objects.equals(pessoaRequest.numero(), pessoa.numero())
        );
    }

    private void limparCampos(){
        campoNomePessoa.setText("* Nome: ");
        campoTelefone.setText("* Fone: ");
        campoEmail.setText("Email: ");
        campoCEP.setText("* CEP: ");
        campoEndereco.setText("Endereço: ");
        campoComplemento.setText("Complemento: ");
        campoBairro.setText("Bairro: ");
        campoNumero.setText("N*: ");
        campoIdade.setText("Idade: ");
        genero.setSelectedItem(GeneroEnum.fromCodigo(0).name());
        campoDataNascimento.setText("Nascimento: ");
        campoRG.setText("RG: ");
        campoCPF.setText("* CPF: ");

        hospedadoSim.setSelected(false);
        hospedadoNao.setSelected(false);

        clienteNovoSim.setSelected(false);
        clienteNovoNao.setSelected(false);

        statusPanel.setBackground(Color.DARK_GRAY);
        statusLabel.setText("Situação");

        quantidadeHospedagemPanel.setVisible(false);

        foto_usuario.set(user_sem_foto_masculino);
        fotoLabel.setIcon(foto_usuario.get());
        foto_usuario_path = "";

        estadoComboBox.setSelectedItem(null);
        municipioComboBox.setSelectedItem(null);
        paisComboBox.setSelectedItem(null);

        genero.addActionListener(e -> generoActionListener());
    }

    private void generoActionListener(){
        String selecionado = (String) genero.getSelectedItem();

        if (selecionado != null) {
            GeneroEnum generoEnum = GeneroEnum.valueOf(selecionado);
            generoSelecionado[0] = generoEnum.ordinal();

            if (generoEnum == GeneroEnum.MASCULINO) {
                foto_usuario.set(user_sem_foto_masculino);
                fotoLabel.setIcon(foto_usuario.get());
                fotoLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ImageIcon darkenedImage = escurecerImagem(foto_usuario.get(), 0.5f);
                        fotoLabel.setIcon(darkenedImage);
                        textoLabel.setVisible(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        fotoLabel.setIcon(foto_usuario.get());
                        textoLabel.setVisible(false);
                    }
                });
            }

            if (generoEnum == GeneroEnum.FEMININO) {
                foto_usuario.set(user_sem_foto_feminino);
                fotoLabel.setIcon(foto_usuario.get());
                fotoLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ImageIcon darkenedImage = escurecerImagem(foto_usuario.get(), 0.5f);
                        fotoLabel.setIcon(darkenedImage);
                        textoLabel.setVisible(true);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        fotoLabel.setIcon(foto_usuario.get());
                        textoLabel.setVisible(false);
                    }
                });
            }
        }
    }

    private MouseAdapter mouseAdapter(){
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon darkenedImage = escurecerImagem(foto_usuario.get(), 0.5f);
                fotoLabel.setIcon(darkenedImage);
                textoLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fotoLabel.setIcon(foto_usuario.get());
                textoLabel.setVisible(false);
            }
        };
    }


    private void desabilitarCamposParaPesquisa(){
        campoNomePessoa.setEditable(false);
        campoTelefone.setEditable(false);
        campoEmail.setEditable(false);
        campoCEP.setEditable(false);
        campoEndereco.setEditable(false);
        campoComplemento.setEditable(false);
        campoBairro.setEditable(false);
        campoNumero.setEditable(false);
        campoIdade.setEditable(false);
        genero.setEditable(false);
        campoDataNascimento.setEditable(false);
        campoRG.setEditable(false);
        campoCPF.setEditable(false);

        hospedadoSim.setEnabled(false);
        hospedadoNao.setEnabled(false);

        clienteNovoSim.setEnabled(false);
        clienteNovoNao.setEnabled(false);

        estadoComboBox.setEnabled(false);
        municipioComboBox.setEnabled(false);
        paisComboBox.setEnabled(false);

        for (ActionListener listener : genero.getActionListeners()) {
            genero.removeActionListener(listener);
        }

        for (MouseListener mouseListener : fotoLabel.getMouseListeners()) {
            fotoLabel.removeMouseListener(mouseListener);
        }
        fotoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        genero.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IdentificacaoPessoaFrame identificacaoPessoaFrame = new IdentificacaoPessoaFrame(null, false);
            identificacaoPessoaFrame.setVisible(true);
        });
    }

}
