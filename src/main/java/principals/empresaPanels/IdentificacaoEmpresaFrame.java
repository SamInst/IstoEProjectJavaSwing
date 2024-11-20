package principals.empresaPanels;

import principals.tools.*;
import repository.EmpresaRepository;
import repository.LocalizacaoRepository;
import repository.PessoaRepository;
import request.AdicionarEmpresaRequest;
import request.BuscaPessoaRequest;
import response.CepInfo;
import response.DadosEmpresaResponse;
import response.Objeto;
import response.PessoaResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static principals.tools.Mascaras.*;

public class IdentificacaoEmpresaFrame extends JFrame {
    private final LocalizacaoRepository localizacaoRepository = new LocalizacaoRepository();
    private final PessoaRepository pessoaRepository = new PessoaRepository();
    private final EmpresaRepository empresaRepository = new EmpresaRepository(pessoaRepository);
    private final ViaCepService viaCepService = new ViaCepService();
    private final CnpjService cnpjService = new CnpjService();

    JTextFieldComTextoFixoArredondado campoNomeEmpresa;
    JTextFieldComTextoFixoArredondado campoBairro;
    JTextFieldComTextoFixoArredondado campoCNPJ;
    JTextFieldComTextoFixoArredondado campoTelefone;
    JTextFieldComTextoFixoArredondado campoEmail;
    JTextFieldComTextoFixoArredondado campoEndereco;
    JTextFieldComTextoFixoArredondado campoNumero;
    JTextFieldComTextoFixoArredondado campoComplemento;
    JTextFieldComTextoFixoArredondado campoCEP;

    JPanel statusPanel;
    JLabel statusLabel;

    private final JComboBoxArredondado<String> paisComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> estadoComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> municipioComboBox = new JComboBoxArredondado<>();
    private final JComboBoxArredondado<String> vincularPessoaComboBox = new JComboBoxArredondado<>();

    private final JPanel listaPessoasVinculadasPanel = new JPanel();
    private final List<BuscaPessoaRequest> pessoasVinculadas = new ArrayList<>();

    public IdentificacaoEmpresaFrame() {
        setTitle("Identificação de Empresa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(750, 510);
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
        campoEndereco.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        adicionarMascaraCNPJ(campoCNPJ);
        campoTelefone = criarCampo("* Fone: ");
        adicionarMascaraTelefone(campoTelefone);
        campoEmail = criarCampo("Email: ");

        campoCEP = new JTextFieldComTextoFixoArredondado("* CEP: ", 5);
        campoCEP.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        adicionarMascaraCEP(campoCEP);

        campoCNPJ.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String cnpj = campoCNPJ.getText()
                        .replace("* CNPJ:", "")
                        .replaceAll("[^0-9]", "");

                if (cnpj.length() == 14) {
                    preencherCamposEmpresa(cnpj);
                }
            }
        });

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 2);
        campoNumero.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 20);
        campoComplemento.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        campoBairro = new JTextFieldComTextoFixoArredondado("Bairro: ", 10);
        campoBairro.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(50, 25));
        statusLabel = new JLabel("Situação");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusPanel.add(statusLabel);

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
                        .addComponent(statusPanel)
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
                                .addComponent(campoBairro)
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
                        .addGap(20)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(statusPanel)
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
                        .addComponent(campoBairro)
                        .addComponent(campoNumero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(paisComboBox)
                        .addComponent(estadoComboBox)
                        .addComponent(municipioComboBox))
                .addGap(30)
                .addComponent(vincularPessoaComboBox)
                .addComponent(listaPessoasVinculadasPanel)
                .addGap(20)
        );

        add(camposPanel, BorderLayout.CENTER);

        JPanel botaoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botaoPanel.add(btnSalvar);
        add(botaoPanel, BorderLayout.SOUTH);
        btnSalvar.addActionListener(e -> {
            try {
                imprimirDadosEmpresa();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

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
                String cnpj = campoCNPJ.getText().trim()
                        .replaceFirst("\\* CNPJ: ", "")
                        .replaceAll("[^0-9]", "");

                if (pessoaRepository.buscaPessoasPorEmpresaCNPJ(cnpj)
                        .stream()
                        .anyMatch(pessoaCadastrada -> pessoaCadastrada.id().equals(pessoa.id()))) {

                    int resposta = JOptionPane.showConfirmDialog(
                            null,
                            "Deseja remover o vínculo para essa pessoa? Nome: " + pessoa.nome(),
                            "Confirmação de Remoção",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (resposta == JOptionPane.YES_OPTION) {
                        try {
                            empresaRepository.desvincularPessoaDaEmpresa(cnpj, pessoa.id());
                        } catch (SQLException e) { e.printStackTrace(); throw new RuntimeException(e);}
                    }
                }

                listaPessoasVinculadasPanel.remove(pessoaPanel);
                listaPessoasVinculadasPanel.revalidate();
                listaPessoasVinculadasPanel.repaint();
                pessoasVinculadas.remove(pessoa);
                pack();
            }
        });

        pessoaPanel.add(contentPanel, BorderLayout.WEST);
        pessoaPanel.add(iconeRemover, BorderLayout.EAST);

        pessoaRepository.buscaPessoasPorEmpresaCNPJ(campoCNPJ.getText() != null ? campoCNPJ.getText() : null)
                .forEach(pessoaCadastrada->{

        });

        listaPessoasVinculadasPanel.add(pessoaPanel);
        pessoasVinculadas.add(pessoa);

        listaPessoasVinculadasPanel.revalidate();
        listaPessoasVinculadasPanel.repaint();
        pack();
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

    private void imprimirDadosEmpresa() throws SQLException {
        String nomeEmpresa = campoNomeEmpresa.getText()
                .replace("* Nome/Razão Social:", "")
                .trim()
                .toUpperCase();

        String cnpj = campoCNPJ.getText()
                .trim()
                .replaceFirst("\\* CNPJ: ", "")
                .replaceAll("[^0-9]", "");

        String telefone = campoTelefone.getText()
                .trim()
                .replaceFirst("\\* Fone: ", "")
                .replaceAll("[^0-9]", "");

        String email = campoEmail.getText()
                .replace("Email:", "")
                .trim()
                .toUpperCase();

        String endereco = campoEndereco.getText()
                .trim()
                .replaceAll("(?i)^endereço:\\s*", "")
                .toUpperCase();

        String cep = campoCEP.getText()
                .trim()
                .replaceFirst("\\* CEP:", "")
                .replace("-", "");

        String numero = campoNumero.getText()
                .trim()
                .replace("N*:", "");

        String complemento = campoComplemento.getText()
                .replace("Complemento:", "")
                .trim()
                .toUpperCase();

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
                cep.trim(),
                numero.trim(),
                complemento,
                pais,
                estado,
                municipio,
                bairro,
                pessoasVinculadasIds
        );

        var pessoasCadastradas = pessoaRepository.buscaPessoasPorEmpresaCNPJ(cnpj);
        int listaPessoas = pessoasVinculadasIds.size();

        pessoasVinculadasIds.removeIf(pessoasVinculadasID -> pessoasCadastradas
                .stream()
                .anyMatch(pessoaCadastrada -> pessoaCadastrada.id().equals(pessoasVinculadasID)));

        if (!empresaRepository.empresaCadastrada(cnpj)){

            empresaRepository.salvarEmpresa(dadosEmpresa);
            JOptionPane.showMessageDialog(this, "Empresa salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } else {
            var empresaCadastrada = empresaRepository.buscarEmpresaPorCnpj(cnpj);

            if(verificaDadosAlterados(empresaCadastrada, dadosEmpresa, pais, estado, municipio)){
                empresaRepository.atualizarDadosDaEmpresa(empresaCadastrada.id(), dadosEmpresa);
                JOptionPane.showMessageDialog(
                        this,
                        "Dados da empresa atualizados com sucesso!",
                        "Atualização",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (pessoasCadastradas.size() != listaPessoas) {
                StringBuilder pessoasAdicionadas = new StringBuilder("Pessoas adicionadas: ");

                pessoasVinculadasIds.forEach(novaPessoa -> {
                    try {
                        empresaRepository.vincularPessoaAEmpresa(cnpj, novaPessoa);

                        PessoaResponse pessoa = pessoaRepository.buscarPessoaPorID(novaPessoa);
                        if (pessoa != null) {
                            pessoasAdicionadas.append(pessoa.nome()).append("\n");
                        }
                    } catch (SQLException e) { throw new RuntimeException(e); }
                });
                JOptionPane.showMessageDialog(null, pessoasAdicionadas.toString(), "Pessoas Adicionadas", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(this, "Empresa já cadastrada!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void preencherEnderecoComCep(String cep) {
        try {
            CepInfo cepInfo = viaCepService.buscarCep(cep);

            if (cepInfo != null) {

                SwingUtilities.invokeLater(() -> {
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



    private void sobrescreverCamposEmpresa(String nomeEmpresa, String telefone, String email, String cep, String endereco, String complemento, String bairro, String numero) {
        campoNomeEmpresa.setText("* Nome/Razão Social: " + nomeEmpresa);
        campoTelefone.setText("* Fone: " + telefone);
        campoEmail.setText("Email: " + email);
        campoCEP.setText("* CEP: " + cep);
        campoEndereco.setText("Endereço: " + endereco);
        campoComplemento.setText("Complemento: " + complemento);
        campoBairro.setText("Bairro: " + bairro);
        campoNumero.setText("N*: " + numero);
    }

    private void preencherCamposEmpresa(String cnpj) {
        try {
            var empresa = cnpjService.buscarEmpresaPorCnpj(cnpj);
            var empresaCadastrada = empresaRepository.buscarEmpresaPorCnpj(cnpj);

            if (verificarCadastro(cnpj)){
                sobrescreverCamposEmpresa(
                        empresaCadastrada.nomeEmpresa(),
                        empresaCadastrada.telefone(),
                        empresaCadastrada.email(),
                        empresaCadastrada.cep(),
                        empresaCadastrada.endereco(),
                        empresaCadastrada.complemento(),
                        empresaCadastrada.bairro(),
                        empresaCadastrada.numero()
                );

                listaPessoasVinculadasPanel.removeAll();
                pessoasVinculadas.clear();

                List<BuscaPessoaRequest> pessoas = pessoaRepository.buscaPessoasPorEmpresaCNPJ(cnpj);
                for (BuscaPessoaRequest pessoa : pessoas) {
                    adicionarPessoaVinculada(pessoa, this);
                }

                var pais = localizacaoRepository.buscarPaisPorId(empresaCadastrada.pais().id());
                var estado = localizacaoRepository.buscarEstadoPorId(empresaCadastrada.estado().id());
                var municipio = localizacaoRepository.buscarMunicipioPorId(empresaCadastrada.municipio().id());

                paisComboBox.setSelectedItem(pais.descricao());
                estadoComboBox.setSelectedItem(estado.descricao());
                municipioComboBox.setSelectedItem(municipio.descricao());

            } else {
                sobrescreverCamposEmpresa(
                        empresa.company().name(),
                        !empresa.phones().isEmpty() ? "(" + empresa.phones().get(0).area() + ") " + empresa.phones().get(0).number() : "",
                        !empresa.emails().isEmpty() ? empresa.emails().get(0).address() : "",
                        empresa.address().zip(),
                        empresa.address().street(),
                        empresa.address().details(),
                        empresa.address().district(),
                        empresa.address().number()
                );

                preencherEnderecoComCep(empresa.address().zip().trim());
                listaPessoasVinculadasPanel.removeAll();
                pessoasVinculadas.clear();
                pack();
            }

            adicionarMascaraCEP(campoCEP);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao buscar informações da empresa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean verificarCadastro(String cnpj) {
        boolean cadastrado = empresaRepository.empresaCadastrada(cnpj);
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

    private boolean verificaDadosAlterados(
            DadosEmpresaResponse empresaCadastrada,
            AdicionarEmpresaRequest adicionarEmpresaRequest,
            Long pais,
            Long estado,
            Long municipio){
        boolean hasChanges = !empresaCadastrada.nomeEmpresa().equalsIgnoreCase(adicionarEmpresaRequest.nomeEmpresa());
        if (!empresaCadastrada.telefone().equals(adicionarEmpresaRequest.telefone())) hasChanges = true;
        if (!empresaCadastrada.email().equalsIgnoreCase(adicionarEmpresaRequest.email())) hasChanges = true;
        if (!empresaCadastrada.endereco().equalsIgnoreCase(adicionarEmpresaRequest.endereco())) hasChanges = true;
        if (!empresaCadastrada.bairro().equalsIgnoreCase(adicionarEmpresaRequest.bairro())) hasChanges = true;
        if (!empresaCadastrada.cep().equals(adicionarEmpresaRequest.cep())) hasChanges = true;
        if (!empresaCadastrada.numero().equals(adicionarEmpresaRequest.numero())) hasChanges = true;
        if (!empresaCadastrada.complemento().equalsIgnoreCase(adicionarEmpresaRequest.complemento())) hasChanges = true;
        if (!pais.equals(empresaCadastrada.pais().id())) hasChanges = true;
        if (!estado.equals(empresaCadastrada.estado().id())) hasChanges = true;
        if (!municipio.equals(empresaCadastrada.municipio().id())) hasChanges = true;
        return hasChanges;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoEmpresaFrame::new);
    }
}
