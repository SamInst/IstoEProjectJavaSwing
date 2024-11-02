package principals.empresaPanels;

import org.hibernate.engine.spi.ValueInclusion;
import principals.tools.Cor;
import principals.tools.JComboBoxArredondado;
import principals.tools.JTextFieldComTextoFixoArredondado;
import principals.tools.Tool;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.AdicionarEmpresaRequest;
import request.BuscaPessoaRequest;
import response.DadosEmpresaResponse;
import response.Objeto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IdentificacaoEmpresaFrame extends JFrame {
    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();

    JTextFieldComTextoFixoArredondado campoNomeEmpresa;
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

        campoNomeEmpresa = criarCampo("Nome/Razão Social: ");
        campoCNPJ = criarCampo("CNPJ: ");
        adicionarMascaraCNPJ(campoCNPJ);
        campoTelefone = criarCampo("Fone: ");
        adicionarMascaraTelefone(campoTelefone);
        campoEmail = criarCampo("Email: ");
        campoEndereco = criarCampo("Endereco: ");
        campoCEP = criarCampo("CEP: ");
        adicionarMascaraCEP(campoCEP);
        campoNumero = criarCampo("N*: ");
        campoComplemento = criarCampo("Complemento: ");

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
                    // Confirma a seleção apenas quando o usuário pressiona Enter
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

        // Efeito de hover
        pessoaPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pessoaPanel.setBackground(new Color(230, 230, 250)); // cor mais clara ao passar o mouse
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pessoaPanel.setBackground(Color.WHITE); // retorna à cor original
            }
        });

        JLabel iconeVinculo = new JLabel(Tool.resizeIcon(new ImageIcon("src/main/resources/icons/chainn.png"), 20, 20));
        JLabel cpfLabel = new JLabel(" " + pessoa.cpf());
        cpfLabel.setForeground(new Color(0x990909));
        JLabel nomeLabel = new JLabel(pessoa.nome());
        nomeLabel.setForeground(Cor.CINZA_ESCURO);

        // Painel para o conteúdo da pessoa
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(iconeVinculo);
        contentPanel.add(cpfLabel);
        contentPanel.add(nomeLabel);

        // Ícone de remoção à direita
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

        // Adiciona os componentes ao painel principal da pessoa
        pessoaPanel.add(contentPanel, BorderLayout.WEST);
        pessoaPanel.add(iconeRemover, BorderLayout.EAST);

        // Adiciona o painel da pessoa ao painel principal de lista
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

    private void adicionarMascaraCNPJ(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String texto = campo.getText().replaceAll("[^0-9]", "");
                if (texto.length() > 14) {
                    texto = texto.substring(0, 14);
                }

                StringBuilder formatado = new StringBuilder("CNPJ: ");
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

                StringBuilder formatado = new StringBuilder("CEP: ");
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
        String nomeEmpresa = campoNomeEmpresa.getText().trim().replace("Nome/Razão Social:", "");
        String cnpj = campoCNPJ.getText().trim().replace("CNPJ:", "");
        String telefone = campoTelefone.getText().trim().replace("Fone:", "");
        String email = campoEmail.getText().trim().replace("Email:", "");
        String endereco = campoEndereco.getText().trim().replace("Endereco:", "");
        String cep = campoCEP.getText().trim().replace("CEP:", "");
        String numero = campoNumero.getText().trim().replace("N*:", "");
        String complemento = campoComplemento.getText().trim().replace("Complemento:", "");

        // Obtenção dos IDs para país, estado e município diretamente
        Long pais = null;
        Long estado = null;
        Long municipio = null;
        try {
            pais = localizacaoRepository.buscaPaisPorNome((String) paisComboBox.getSelectedItem()).id();
            estado = localizacaoRepository.buscaEstadoPorNomeEId((String) estadoComboBox.getSelectedItem(), pais).id();
            municipio = localizacaoRepository.buscaMunicipioPorNomeEId((String) municipioComboBox.getSelectedItem(), estado).id();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Coleta os IDs das pessoas vinculadas
        List<Long> pessoasVinculadasIds = new ArrayList<>();
        for (BuscaPessoaRequest pessoa : pessoasVinculadas) {
            pessoasVinculadasIds.add(pessoa.id());
        }

        // Criação da instância de AdicionarEmpresaRequest com os dados coletados
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

        // Impressão do objeto para visualização dos dados no console
        System.out.println(dadosEmpresa);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoEmpresaFrame::new);
    }
}
