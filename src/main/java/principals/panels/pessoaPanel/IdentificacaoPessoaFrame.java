package principals.panels.pessoaPanel;

import enums.GeneroEnum;
import principals.tools.*;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.PessoaRequest;
import response.CepInfo;
import response.Objeto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static principals.tools.EscurecerImagemDemo.escurecerImagem;
import static principals.tools.Mascaras.*;

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

    Font font = new Font("Segoe UI", Font.PLAIN, 15);
    final int[] generoSelecionado = new int[1];

    ImageIcon foto_usuario = Resize.resizeIcon(Icones.user_sem_foto, 180,180);

    public IdentificacaoPessoaFrame() {
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
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
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
        adicionarMascaraCPF(campoCPF);

        campoRG = new JTextFieldComTextoFixoArredondado("RG: ", 10);
        campoRG.setPreferredSize(fieldDimension);
        adicionarMascaraRG(campoRG);

        campoIdade = new JTextFieldComTextoFixoArredondado("Idade: ", 10);
        campoIdade.setPreferredSize(fieldDimension);

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
        fotoPanel.setPreferredSize(new Dimension(180, 180));
        fotoPanel.setMaximumSize(new Dimension(180, 180));
        fotoPanel.setLayout(new OverlayLayout(fotoPanel));

        JLabel textoLabel = new JLabel("Alterar imagem");
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
        fotoLabel.setIcon(foto_usuario);

        fotoPanel.add(fotoLabel, BorderLayout.CENTER);
        painelEsquerdo.add(fotoPanel, BorderLayout.CENTER);

        fotoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon darkenedImage = escurecerImagem(foto_usuario, 0.5f);
                fotoLabel.setIcon(darkenedImage);
                textoLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fotoLabel.setIcon(foto_usuario);
                textoLabel.setVisible(false);
            }
        });

        genero.addActionListener(e -> {
            String selecionado = (String) genero.getSelectedItem();

            if (selecionado != null) {
                GeneroEnum generoEnum = GeneroEnum.valueOf(selecionado);
                generoSelecionado[0] = generoEnum.ordinal() + 1;
            }
            if (selecionado != null) {
                GeneroEnum generoEnum = GeneroEnum.valueOf(selecionado);
                if (generoEnum == GeneroEnum.MASCULINO) {
                    foto_usuario = Resize.resizeIcon(Icones.user_sem_foto, 180,180);
                    fotoLabel.setIcon(foto_usuario);
                    fotoLabel.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            ImageIcon darkenedImage = escurecerImagem(foto_usuario, 0.5f);
                            fotoLabel.setIcon(darkenedImage);
                            textoLabel.setVisible(true);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            fotoLabel.setIcon(foto_usuario);
                            textoLabel.setVisible(false);
                        }
                    });
                }

                if (generoEnum == GeneroEnum.FEMININO) {
                    foto_usuario = Resize.resizeIcon(Icones.user_sem_foto_feminino, 180,180);
                    fotoLabel.setIcon(foto_usuario);
                    fotoLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            ImageIcon darkenedImage = escurecerImagem(foto_usuario, 0.5f);
                            fotoLabel.setIcon(darkenedImage);
                            textoLabel.setVisible(true);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            fotoLabel.setIcon(foto_usuario);
                            textoLabel.setVisible(false);
                        }
                    });
                }
            }
        });

        fotoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\IstoEPousada\\Usuarios\\Fotos"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                javax.swing.filechooser.FileNameExtensionFilter imageFilter =
                        new javax.swing.filechooser.FileNameExtensionFilter("Imagens (JPG, JPEG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(imageFilter);

                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    foto_usuario_path = selectedFile.getAbsolutePath();
                    try {
                        ImageIcon selectedImage = new ImageIcon(selectedFile.getAbsolutePath());
                        foto_usuario = Resize.resizeIcon(selectedImage, 180, 180);
                        fotoLabel.setIcon(foto_usuario);

                        for (ActionListener listener : genero.getActionListeners()) {
                            genero.removeActionListener(listener);
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao carregar a imagem.", "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        statusPanel = new JPanel();
        statusPanel.setBackground(Color.DARK_GRAY);
        statusPanel.setPreferredSize(new Dimension(200, 30));
        statusPanel.setMaximumSize(new Dimension(250, 30));
        statusLabel = new JLabel("Situação");
        statusLabel.setFont(font);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
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
                System.out.println(dataNascimentoTexto);

                if (dataNascimentoTexto.length() == 10) {
                    try {
                        LocalDate dataNascimento = LocalDate.parse(dataNascimentoTexto, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        int idadeCalculada = Period.between(dataNascimento, LocalDate.now()).getYears();
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
        clienteNovoPanel.setForeground(Cor.CINZA_CLARO);
        clienteNovoPanel.setFont(font);
        clienteNovoPanel.add(clienteNovoSim);
        clienteNovoPanel.add(clienteNovoNao);

        painelEsquerdo.add(clienteNovoPanel);

        JPanel hospedadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hospedadoPanel.setPreferredSize(new Dimension(200, 30));
        hospedadoPanel.setMaximumSize(new Dimension(250, 30));
        hospedadoPanel.add(new JLabel("Está Hospedado?"));
        hospedadoPanel.setBackground(Color.WHITE);
        hospedadoPanel.setForeground(Cor.CINZA_CLARO);
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
            @Override
            public void keyReleased(KeyEvent e) {
                String cpf = campoCPF.getText()
                        .replace("* CPF:", "").trim();

                if (cpf.length() == 14) {
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

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setFont(font);
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBackground(Color.YELLOW.darker());
        btnLimpar.setForeground(Color.WHITE);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(font);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botaoPanel.add(btnSalvar);

        btnSalvar.addActionListener(a->{
            try {
                adicionarPessoa();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

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

    public void adicionarPessoa() throws SQLException {
            String nomeEmpresa = campoNomePessoa.getText()
                    .replace("* Nome:","")
                    .trim()
                    .toUpperCase();

        String dataNascimento = campoDataNascimento.getText()
                .replace("Nascimento: ", "")
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
                .replaceFirst("\\* Fone: ", "")
                .replaceAll("[^0-9]", "");

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
                .replace("-", "")
                .trim();

        String bairro = campoBairro.getText()
                .replace("Bairro:", "")
                .trim()
                .toUpperCase();

        Long pais = null;
        Long estado = null;
        Long municipio = null;

        try {
            pais = paisComboBox.getSelectedItem() != null ? localizacaoRepository.buscaPaisPorNome((String) paisComboBox.getSelectedItem()).id() : null;
            estado = estadoComboBox.getSelectedItem() != null ? localizacaoRepository.buscaEstadoPorNomeEId((String) estadoComboBox.getSelectedItem(), pais).id() : null;
            municipio = municipioComboBox.getSelectedItem() != null ? localizacaoRepository.buscaMunicipioPorNomeEId((String) municipioComboBox.getSelectedItem(), estado).id() : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (nomeEmpresa.isEmpty()){
            JOptionPane.showMessageDialog(this, "Nome da Pessoa obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (telefone.isEmpty()){
            JOptionPane.showMessageDialog(this, "Telefone para contato obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cpf.isEmpty()){
            JOptionPane.showMessageDialog(this, "CPF obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cep.isEmpty()){
            JOptionPane.showMessageDialog(this, "CEP obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pais == null){
            JOptionPane.showMessageDialog(this, "País obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (estado == null){
            JOptionPane.showMessageDialog(this, "Estado obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (municipio == null){
            JOptionPane.showMessageDialog(this, "Município obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

            PessoaRequest pessoa = new PessoaRequest(
                    foto_usuario_path,
                    nomeEmpresa,
                    novaDataNascimento,
                    25,
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

        System.out.println(pessoa);
           try {
               var pessoaID = pessoaRepository.adicionarPessoa(pessoa);
               pessoaRepository.adicionarFotoPessoa(pessoaID, foto_usuario_path);

               JOptionPane.showMessageDialog(this, "Pessoa adicionada com sucesso!");
        } catch (Exception e) {
               JOptionPane.showMessageDialog(this, "Erro ao adicionar pessoa. Verifique os dados e tente novamente.");
            e.printStackTrace();
        }
    }

    private boolean verificaCPF(String cpf) throws SQLException {
        return pessoaRepository.cpfExists(cpf);
    }

    private void preencherEnderecoComCep(String cep) {
        try {
            CepInfo cepInfo = viaCepService.buscarCep(cep);
            System.out.println(cepInfo);

            if (cepInfo != null) {

                SwingUtilities.invokeLater(() -> {

                    campoEndereco.setText("Endereço: " + cepInfo.logradouro() +", " + cepInfo.unidade());
                    campoBairro.setText("Bairro: " + cepInfo.bairro());
                    campoNumero.setText("* CEP: " + cepInfo.complemento());


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
            e.printStackTrace();
        }
    }

    private boolean verificarCadastro(String cpf) throws SQLException {
        boolean cadastrado = pessoaRepository.cpfExists(cpf);
        if (cadastrado) {
            statusLabel.setText("Cadastrado");
            statusPanel.setBackground(Cor.VERDE_ESCURO);
            statusLabel.setForeground(Color.WHITE);
        } else {
            statusLabel.setText("Não cadastrado");
            statusPanel.setBackground(Cor.VERMELHO);
            statusLabel.setForeground(Color.WHITE);
        }
        return cadastrado;
    }

    private void sobrescreverCamposPessoa(String nomePessoa, String telefone, String email, String cep, String endereco, String complemento, String bairro, String numero, Boolean hospedado, Boolean clienteNovo, ImageIcon foto, Integer idade, Integer sexo, String dataNascimento, String rg) {
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
        foto_usuario = foto;
        fotoLabel.setIcon(foto);

    }

    private void preencherCamposPessoa(String cpf) {
        try {
            var pessoa = pessoaRepository.buscarPessoaPorCPF(cpf);
            if (pessoa != null) {

                var pessoaCadastrada = verificaCPF(cpf);
                var foto = pessoaRepository.buscarFotoPessoaPorId(pessoa.id());

                verificarCadastro(cpf);
                if (pessoaCadastrada){
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
                            pessoa.data_nascimento().formatted(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            pessoa.rg()
                    );

                    var pais = localizacaoRepository.buscarPaisPorId(pessoa.pais().id());
                    var estado = localizacaoRepository.buscarEstadoPorId(pessoa.estado().id());
                    var municipio = localizacaoRepository.buscarMunicipioPorId(pessoa.municipio().id());

                    paisComboBox.setSelectedItem(pais.descricao());
                    estadoComboBox.setSelectedItem(estado.descricao());
                    municipioComboBox.setSelectedItem(municipio.descricao());
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



    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoPessoaFrame::new);
    }
}
