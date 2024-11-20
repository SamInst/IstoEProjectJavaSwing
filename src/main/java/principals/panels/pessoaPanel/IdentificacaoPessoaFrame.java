package principals.panels.pessoaPanel;

import principals.tools.*;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.PessoaRequest;
import response.Objeto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static principals.tools.EscurecerImagemDemo.escurecerImagem;
import static principals.tools.Mascaras.*;

public class IdentificacaoPessoaFrame extends JFrame {
    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();

    JTextFieldComTextoFixoArredondado campoNome;
    JTextFieldComTextoFixoArredondado campoCPF;
    JTextFieldComTextoFixoArredondado campoRG;
    JTextFieldComTextoFixoArredondado campoTelefone;
    JTextFieldComTextoFixoArredondado campoEmail;
    JTextFieldComTextoFixoArredondado campoDataNascimento;
    JTextFieldComTextoFixoArredondado campoEndereco;
    JTextFieldComTextoFixoArredondado campoNumero;
    JTextFieldComTextoFixoArredondado campoComplemento;
    JTextFieldComTextoFixoArredondado campoCEP;

    private final JComboBoxArredondado<String> paisComboBox;
    private final JComboBoxArredondado<String> estadoComboBox;
    private final JComboBoxArredondado<String> municipioComboBox;
    private final JComboBoxArredondado<String> genero;

     JRadioButton hospedadoSim;
     JRadioButton hospedadoNao;
     JRadioButton clienteNovoSim;
     JRadioButton clienteNovoNao;
     JPanel statusPanel;
     JLabel statusLabel;

    public IdentificacaoPessoaFrame() {
        setTitle("Identificação de Pessoa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 500);
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
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel painelEsquerdo = new JPanel();
        painelEsquerdo.setLayout(new BoxLayout(painelEsquerdo, BoxLayout.Y_AXIS));
        painelEsquerdo.setBackground(Color.LIGHT_GRAY);
        painelEsquerdo.setPreferredSize(new Dimension(220, 550));
        add(painelEsquerdo, BorderLayout.WEST);

        JPanel fotoPanel = new JPanel();
        fotoPanel.setBackground(Color.RED);
        fotoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fotoPanel.setPreferredSize(new Dimension(150, 150));
        fotoPanel.setMaximumSize(new Dimension(150, 150));
        fotoPanel.setLayout(new OverlayLayout(fotoPanel));

        JLabel textoLabel = new JLabel("Alterar imagem");
        textoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        textoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textoLabel.setVerticalAlignment(SwingConstants.CENTER);
        textoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textoLabel.setForeground(new Color(255, 255, 255, 200));
        textoLabel.setVisible(false);
        fotoPanel.add(textoLabel);

        var user_sem_foto = Resize.resizeIcon(Icones.user_sem_foto, 150,150);

        JLabel fotoLabel = new JLabel();
        fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fotoLabel.setVerticalAlignment(SwingConstants.CENTER);
        fotoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fotoLabel.setIcon(user_sem_foto);

        fotoPanel.add(fotoLabel, BorderLayout.CENTER);
        painelEsquerdo.add(fotoPanel, BorderLayout.CENTER);

        fotoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ImageIcon darkenedImage = escurecerImagem(user_sem_foto, 0.5f);
                fotoLabel.setIcon(darkenedImage);
                textoLabel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fotoLabel.setIcon(user_sem_foto);
                textoLabel.setVisible(false);
            }
        });





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
        adicionarMascaraData(campoDataNascimento);

        campoEndereco = new JTextFieldComTextoFixoArredondado("Endereco: ", 20);
        campoEndereco.setPreferredSize(fieldDimension);

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 5);
        campoNumero.setPreferredSize(fieldDimension);

        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 25);
        campoComplemento.setPreferredSize(fieldDimension);

        campoCEP = new JTextFieldComTextoFixoArredondado("* CEP: ", 5);
        campoCEP.setFont(new Font("Segoe UI", Font.PLAIN, 15));
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

        statusPanel = new JPanel();
        statusPanel.setBackground(Color.GREEN);
        statusPanel.setPreferredSize(new Dimension(200, 30));
        statusPanel.setMaximumSize(new Dimension(250, 30));
        statusLabel = new JLabel("Situação");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
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

//        JPanel hospedadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        hospedadoPanel.add(new JLabel("Está Hospedado?"));
//        hospedadoPanel.setBackground(Color.WHITE);
//        hospedadoPanel.setForeground(Cor.CINZA_CLARO);
//        hospedadoPanel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        hospedadoPanel.add(hospedadoSim);
//        hospedadoPanel.add(hospedadoNao);



        JPanel clienteNovoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clienteNovoPanel.setPreferredSize(new Dimension(200, 30));
        clienteNovoPanel.setMaximumSize(new Dimension(250, 30));
        clienteNovoPanel.add(new JLabel("Cliente Novo?"));
        clienteNovoPanel.setBackground(Color.WHITE);
        clienteNovoPanel.setForeground(Cor.CINZA_CLARO);
        clienteNovoPanel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        clienteNovoPanel.add(clienteNovoSim);
        clienteNovoPanel.add(clienteNovoNao);

        painelEsquerdo.add(clienteNovoPanel);

        JPanel hospedadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hospedadoPanel.setPreferredSize(new Dimension(200, 30));
        hospedadoPanel.setMaximumSize(new Dimension(250, 30));
        hospedadoPanel.add(new JLabel("Está Hospedado?"));
        hospedadoPanel.setBackground(Color.WHITE);
        hospedadoPanel.setForeground(Cor.CINZA_CLARO);
        hospedadoPanel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        hospedadoPanel.add(hospedadoSim);
        hospedadoPanel.add(hospedadoNao);

        painelEsquerdo.add(hospedadoPanel);



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
                        .addGap(50)
                        .addComponent(campoEndereco)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoComplemento)
                                .addComponent(campoNumero))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(paisComboBox)
                                .addComponent(estadoComboBox)
                                .addComponent(municipioComboBox))
//                        .addComponent(hospedadoPanel)
//                        .addComponent(clienteNovoPanel)
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
//                .addComponent(hospedadoPanel)
//                .addComponent(clienteNovoPanel)
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
            String nome = campoNome.getText().replace("Nome:","").trim().toUpperCase();
            LocalDate dataNascimento = LocalDate.parse(campoDataNascimento.getText().replace("Nascimento: ", "").trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String cpf = campoCPF.getText().replace("CPF: ", "").trim();
            String rg = campoRG.getText().replace("RG: ", "").trim();
            String email = campoEmail.getText().replace("Email:", "").trim().toUpperCase();
            String telefone = campoTelefone.getText().replace("Fone: ", "").trim();

            Long pais = localizacaoRepository.buscaPaisPorNome((String) paisComboBox.getSelectedItem()).id();
            Long estado = localizacaoRepository.buscaEstadoPorNomeEId((String) estadoComboBox.getSelectedItem(), pais).id();
            Long municipio = localizacaoRepository.buscaMunicipioPorNomeEId((String) municipioComboBox.getSelectedItem(), estado).id();

            String endereco = campoEndereco.getText().replace("Endereco:", "").trim().toUpperCase();
            String complemento = campoComplemento.getText().replace("Complemento: ", "").trim().toUpperCase();
            Boolean hospedado = hospedadoSim.isSelected();
            Integer vezesHospedado = hospedado ? + 1 : 0;
            Boolean clienteNovo = clienteNovoSim.isSelected();


            PessoaRequest pessoa = new PessoaRequest(
                    nome,
                    dataNascimento,
                    cpf,
                    rg,
                    email,
                    telefone,
                    pais,
                    estado,
                    municipio,
                    endereco,
                    complemento,
                    hospedado,
                    vezesHospedado,
                    clienteNovo
            );

           try {
               pessoaRepository.adicionarPessoa(pessoa);

               System.out.println(pessoa);
               JOptionPane.showMessageDialog(this, "Pessoa adicionada com sucesso!");

        } catch (Exception e) {
               JOptionPane.showMessageDialog(this, "Erro ao adicionar pessoa. Verifique os dados e tente novamente.");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoPessoaFrame::new);
    }
}
