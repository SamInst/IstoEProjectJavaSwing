package principals.empresaPanels;

import principals.tools.*;
import repository.EmpresaRepository;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.AdicionarEmpresaRequest;
import request.BuscaPessoaRequest;
import response.CepInfo;
import response.Objeto;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IdentificacaoEmpresaFrame extends JFrame {
    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();
    private final EmpresaRepository empresaRepository = new EmpresaRepository();
    private final ViaCepService viaCepService = new ViaCepService();

    JTextFieldComTextoFixoArredondado campoNomeEmpresa;
    JTextFieldComTextoFixoArredondado campoBairro;
    JTextFieldComTextoFixoArredondado campoCNPJ;
    JTextFieldComTextoFixoArredondado campoTelefone;
    JTextFieldComTextoFixoArredondado campoEmail;
    JTextFieldComTextoFixoArredondado campoEndereco;
    JTextFieldComTextoFixoArredondado campoNumero;
    JTextFieldComTextoFixoArredondado campoComplemento;
    JTextFieldComTextoFixoArredondado campoCEP;

    private final JComboBoxArredondado<String> paisComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> estadoComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> municipioComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> vincularPessoaComboBox = new JComboBoxArredondado<>();

    private final JPanel listaPessoasVinculadasPanel = new JPanel();
    private final List<BuscaPessoaRequest> pessoasVinculadas = new ArrayList<>();

    public IdentificacaoEmpresaFrame() {
        setTitle("Identificação de Empresa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(750, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(700, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
        JLabel titulo = new JLabel("Identificação de Empresa");
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel camposPanel = new JPanel();
        camposPanel.setBackground(Color.WHITE);

        campoNomeEmpresa = criarCampo("* Nome/Razão Social: ");
        campoCNPJ = criarCampo("* CNPJ: ");
        campoEndereco = new JTextFieldComTextoFixoArredondado("Endereço: ", 35);
//        campoEndereco.setPreferredSize(new Dimension(200, 25));
        campoEndereco.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        adicionarMascaraCNPJ(campoCNPJ);
        campoTelefone = criarCampo("* Fone: ");
        adicionarMascaraTelefone(campoTelefone);
        campoEmail = criarCampo("Email: ");
//        campoEndereco = criarCampo("Endereco: ");


        campoCEP = new JTextFieldComTextoFixoArredondado("* CEP: ", 5);
        campoCEP.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        campoCEP.setPreferredSize(new Dimension(120, 25));
        adicionarMascaraCEP(campoCEP);

        campoCEP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String cep = campoCEP.getText().trim().replace("* CEP:", "").replace("-", "").replaceAll("[^0-9]", "");

                if (cep.length() == 8) {
                    System.out.println("Buscando informações para o CEP: " + cep);
                    preencherEnderecoComCep(cep);
                    campoEndereco.setText("AAAAAAAAAAAAAAAAAAAAAAAAAA");
                }
            }
        });

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 2);
        campoNumero.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 20);
        campoComplemento.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        campoBairro = new JTextFieldComTextoFixoArredondado("Bairro: ", 10);
        campoBairro.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        paisComboBox.setPreferredSize(new Dimension(190, 30));
        estadoComboBox.setPreferredSize(new Dimension(190, 30));
        municipioComboBox.setPreferredSize(new Dimension(190, 30));
        paisComboBox.setFont(font);
        estadoComboBox.setFont(font);
        municipioComboBox.setFont(font);

        vincularPessoaComboBox.setPreferredSize(new Dimension(400, 25));
        vincularPessoaComboBox.setFont(font);
        vincularPessoaComboBox.setForeground(Cor.CINZA_ESCURO);
        vincularPessoaComboBox.setEditable(true);


        JTextField editor = (JTextField) vincularPessoaComboBox.getEditor().getEditorComponent();
        mascaraUpperCase(editor);
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = editor.getText();
                if (query.length() >= 3) {
                    buscarPessoa(query);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String selecionado = (String) vincularPessoaComboBox.getSelectedItem();
                    if (selecionado != null && !selecionado.isEmpty()) {
                        List<BuscaPessoaRequest> pessoas = pessoaRepository.buscarPessoaPorIdNomeOuCpf(selecionado.split(" - ")[0]);
                        if (!pessoas.isEmpty()) {
                            adicionarPessoaVinculada(pessoas.get(0), IdentificacaoEmpresaFrame.this);
                        }
                        vincularPessoaComboBox.setSelectedItem("");
                    }
                }
            }
        });

        carregarPaises();

        paisComboBox.addActionListener(e -> carregarEstados());
        estadoComboBox.addActionListener(e -> carregarMunicipios());

        listaPessoasVinculadasPanel.setLayout(new BoxLayout(listaPessoasVinculadasPanel, BoxLayout.Y_AXIS));
        listaPessoasVinculadasPanel.setBackground(Color.WHITE);

        GroupLayout layout = new GroupLayout(camposPanel);
        camposPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(campoNomeEmpresa)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoCNPJ)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoTelefone))
                        .addComponent(campoEmail)
                        .addGap(50)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoEndereco)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoCEP))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoComplemento)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoBairro) // Adiciona campoBairro ao lado de campoComplemento
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(campoNumero))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(paisComboBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(estadoComboBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(municipioComboBox))
                        .addGap(30)
                        .addComponent(vincularPessoaComboBox)
                        .addComponent(listaPessoasVinculadasPanel)
                        .addGap(150)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(campoNomeEmpresa)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoCNPJ)
                        .addComponent(campoTelefone))
                .addComponent(campoEmail)
                .addGap(50)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoEndereco)
                        .addComponent(campoCEP))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoComplemento)
                        .addComponent(campoBairro) // Adiciona o campoBairro na posição correta
                        .addComponent(campoNumero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(paisComboBox)
                        .addComponent(estadoComboBox)
                        .addComponent(municipioComboBox))
                .addGap(30)
                .addComponent(vincularPessoaComboBox)
                .addComponent(listaPessoasVinculadasPanel)
                .addGap(150)
        );

        add(camposPanel, BorderLayout.CENTER);

        JPanel botaoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botaoPanel.add(btnSalvar);
        add(botaoPanel, BorderLayout.SOUTH);
        btnSalvar.addActionListener(e -> imprimirDadosEmpresa());

        setVisible(true);
    }

    private void buscarPessoa(String query) {
        vincularPessoaComboBox.removeAllItems();
        List<BuscaPessoaRequest> pessoas = pessoaRepository.buscarPessoaPorIdNomeOuCpf(query);
        for (BuscaPessoaRequest pessoa : pessoas) {
            vincularPessoaComboBox.addItem(pessoa.nome() + " - " + pessoa.cpf());
        }
        vincularPessoaComboBox.getEditor().setItem(query);
        vincularPessoaComboBox.showPopup();
    }

    private void adicionarPessoaVinculada(BuscaPessoaRequest pessoa, JFrame frame) {
        JPanel pessoaPanel = new JPanel();
        pessoaPanel.setLayout(new BorderLayout());
        pessoaPanel.setBackground(Color.WHITE);
        pessoaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pessoaPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pessoaPanel.setBackground(new Color(230, 230, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pessoaPanel.setBackground(Color.WHITE);
            }
        });

        JLabel iconeVinculo = new JLabel(Tool.resizeIcon(new ImageIcon("src/main/resources/icons/chainn.png"), 20, 20));
        JLabel cpfLabel = new JLabel(" " + pessoa.cpf());
        cpfLabel.setForeground(new Color(0x990909));
        JLabel nomeLabel = new JLabel(pessoa.nome());
        nomeLabel.setForeground(Cor.CINZA_ESCURO);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(iconeVinculo);
        contentPanel.add(cpfLabel);
        contentPanel.add(nomeLabel);

        JLabel iconeRemover = new JLabel(Tool.resizeIcon(new ImageIcon("src/main/resources/icons/remove.png"), 20, 20));
        iconeRemover.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconeRemover.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                listaPessoasVinculadasPanel.remove(pessoaPanel);
                listaPessoasVinculadasPanel.revalidate();
                listaPessoasVinculadasPanel.repaint();
                pessoasVinculadas.remove(pessoa);
            }
        });

        pessoaPanel.add(contentPanel, BorderLayout.WEST);
        pessoaPanel.add(iconeRemover, BorderLayout.EAST);

        listaPessoasVinculadasPanel.add(pessoaPanel);
        pessoasVinculadas.add(pessoa);

        listaPessoasVinculadasPanel.revalidate();
        listaPessoasVinculadasPanel.repaint();

        frame.revalidate();
        frame.repaint();
    }


    private JTextFieldComTextoFixoArredondado criarCampo(String texto) {
        JTextFieldComTextoFixoArredondado campo = new JTextFieldComTextoFixoArredondado(texto, 30);
        campo.setPreferredSize(new Dimension(200, 25));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return campo;
    }



    private void carregarPaises() {
        try {
            List<Objeto> paises = localizacaoRepository.buscarPaises();
            paisComboBox.removeAllItems();
            paises.forEach(pais -> paisComboBox.addItem(pais.descricao()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarEstados() {
        String paisSelecionado = (String) paisComboBox.getSelectedItem();
        if (paisSelecionado != null) {
            try {
                Objeto pais = localizacaoRepository.buscaPaisPorNome(paisSelecionado);
                estadoComboBox.removeAllItems();
                List<Objeto> estados = localizacaoRepository.buscarEstadosPorPaisId(pais.id());
                estados.forEach(estado -> estadoComboBox.addItem(estado.descricao()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarMunicipios() {
        String estadoSelecionado = (String) estadoComboBox.getSelectedItem();
        if (estadoSelecionado != null) {
            try {
                Objeto estado = localizacaoRepository.buscaEstadoPorNomeEId(estadoSelecionado, getPaisIdByName((String) paisComboBox.getSelectedItem()));
                municipioComboBox.removeAllItems();
                List<Objeto> municipios = localizacaoRepository.buscarMunicipiosPorEstadoId(estado.id());
                municipios.forEach(municipio -> municipioComboBox.addItem(municipio.descricao()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private long getPaisIdByName(String paisNome) {
        try {
            Objeto pais = localizacaoRepository.buscaPaisPorNome(paisNome);
            return pais != null ? pais.id() : 0L;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private void adicionarMascaraTelefone(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 11) {
                    texto = texto.substring(0, 11);
                }

                StringBuilder formatado = new StringBuilder("* Fone: ");
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

    private void adicionarMascaraCNPJ(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 14) {
                    texto = texto.substring(0, 14);
                }

                StringBuilder formatado = new StringBuilder("* CNPJ: ");
                if (texto.length() > 2) {
                    formatado.append(texto, 0, 2).append(".");
                } else {
                    formatado.append(texto);
                }
                if (texto.length() > 5) {
                    formatado.append(texto, 2, 5).append(".");
                } else if (texto.length() > 2) {
                    formatado.append(texto.substring(2));
                }
                if (texto.length() > 8) {
                    formatado.append(texto, 5, 8).append("/");
                } else if (texto.length() > 5) {
                    formatado.append(texto.substring(5));
                }
                if (texto.length() > 12) {
                    formatado.append(texto, 8, 12).append("-");
                } else if (texto.length() > 8) {
                    formatado.append(texto.substring(8));
                }
                if (texto.length() > 12) {
                    formatado.append(texto.substring(12));
                }

                campo.setText(formatado.toString());
            }
        });
    }

    private void adicionarMascaraCEP(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 8) {
                    texto = texto.substring(0, 8);
                }

                StringBuilder formatado = new StringBuilder("* CEP: ");
                if (texto.length() > 5) {
                    formatado.append(texto, 0, 5).append("-").append(texto.substring(5));
                } else {
                    formatado.append(texto);
                }

                campo.setText(formatado.toString());
            }
        });
    }

    private void imprimirDadosEmpresa() {
        String nomeEmpresa = campoNomeEmpresa.getText().trim().replace("* Nome/Razão Social:", "").toUpperCase();
        String cnpj = campoCNPJ.getText().trim()
                .replaceFirst("\\* CNPJ: ", "")
                .replaceAll("[^0-9]", "");

        String telefone = campoTelefone.getText().trim()
                .replaceFirst("\\* Fone: ", "")
                .replaceAll("[^0-9]", "");
        String email = campoEmail.getText().trim().replace("Email:", "").toUpperCase();
        String endereco = campoEndereco.getText().trim().replace("Endereco:", "").toUpperCase();
        String cep = campoCEP.getText().trim()
                .replaceFirst("\\* CEP:", "")
                .replace("-", "");
        String numero = campoNumero.getText().trim().replace("N*:", "");
        String complemento = campoComplemento.getText().trim().replace("Complemento:", "").toUpperCase();

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

        List<Long> pessoasVinculadasIds = new ArrayList<>();
        for (BuscaPessoaRequest pessoa : pessoasVinculadas) {
            pessoasVinculadasIds.add(pessoa.id());
        }

        if (nomeEmpresa.isEmpty()){
            JOptionPane.showMessageDialog(this, "Nome da Empresa obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (telefone.isEmpty()){
            JOptionPane.showMessageDialog(this, "Telefone para contato obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cnpj.isEmpty()){
            JOptionPane.showMessageDialog(this, "CNPJ obrigatório", "Aviso", JOptionPane.WARNING_MESSAGE);
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

        AdicionarEmpresaRequest dadosEmpresa = new AdicionarEmpresaRequest(
                nomeEmpresa,
                cnpj,
                telefone,
                email,
                endereco,
                cep,
                numero,
                complemento,
                pais,
                estado,
                municipio,
                pessoasVinculadasIds
        );

        try {
            empresaRepository.salvarEmpresa(dadosEmpresa);
            JOptionPane.showMessageDialog(this, "Empresa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void preencherEnderecoComCep(String cep) {
        try {
            CepInfo cepInfo = viaCepService.buscarCep(cep);

            if (cepInfo != null) {
                System.out.println("CEP encontrado: " + cepInfo);

                String enderecoCompleto = "";
                if (cepInfo.logradouro() != null && !cepInfo.logradouro().isEmpty()) {
                    enderecoCompleto += cepInfo.logradouro();
                }
                if (cepInfo.bairro() != null && !cepInfo.bairro().isEmpty()) {
                    enderecoCompleto += (enderecoCompleto.isEmpty() ? "" : ", ") + cepInfo.bairro();
                }


                String finalEnderecoCompleto = enderecoCompleto;



                SwingUtilities.invokeLater(() -> {
                    campoEndereco.setText("Endereço: " + cepInfo.logradouro());
                    campoComplemento.setText("Complemento: " + (cepInfo.complemento() != null ? cepInfo.complemento() : ""));
                    campoBairro.setText("Bairro: " + cepInfo.bairro());
                });

                SwingUtilities.invokeLater(() -> {
                    paisComboBox.setSelectedItem("Brasil");
                    Objeto estado = null;
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

    public void mascaraUpperCase(JTextField textField) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new UpperCaseDocumentFilter());
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoEmpresaFrame::new);
    }
}
