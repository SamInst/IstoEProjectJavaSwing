package principals.panels.pessoaPanel;

import principals.tools.JTextFieldComTextoFixoArredondado;

import javax.swing.*;
import java.awt.*;

public class IdentificacaoPessoaFrame extends JFrame {

    private final JTextFieldComTextoFixoArredondado campoNome;
    private final JTextFieldComTextoFixoArredondado campoCPF;
    private final JTextFieldComTextoFixoArredondado campoRG;
    private final JTextFieldComTextoFixoArredondado campoTelefone;
    private final JTextFieldComTextoFixoArredondado campoEmail;
    private final JTextFieldComTextoFixoArredondado campoDataNascimento;
    private final JTextFieldComTextoFixoArredondado campoEndereco;
    private final JTextFieldComTextoFixoArredondado campoNumero;
    private final JTextFieldComTextoFixoArredondado campoComplemento;
    private final JTextFieldComTextoFixoArredondado campoPais;
    private final JTextFieldComTextoFixoArredondado campoEstado;
    private final JTextFieldComTextoFixoArredondado campoMunicipio;

    public IdentificacaoPessoaFrame() {
        setTitle("Identificação de Pessoa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 450);
        setPreferredSize(new Dimension(600, 450));
        setMinimumSize(new Dimension(600, 450));
        setResizable(false);
        setLocationRelativeTo(null);

        // Painel para o título
        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(600, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));
        JLabel titulo = new JLabel("Identificação de Pessoa");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        // Painel central para os campos
        JPanel camposPanel = new JPanel();
        camposPanel.setBackground(Color.WHITE);

        // Definir altura uniforme para os campos
        Dimension fieldDimension = new Dimension(200, 25);

        campoNome = new JTextFieldComTextoFixoArredondado("Nome: ", 30);
        campoNome.setPreferredSize(fieldDimension);

        campoCPF = new JTextFieldComTextoFixoArredondado("CPF: ", 10);
        campoCPF.setPreferredSize(fieldDimension);

        campoRG = new JTextFieldComTextoFixoArredondado("RG: ", 10);
        campoRG.setPreferredSize(fieldDimension);

        campoTelefone = new JTextFieldComTextoFixoArredondado("Fone: ", 15);
        campoTelefone.setPreferredSize(fieldDimension);

        campoEmail = new JTextFieldComTextoFixoArredondado("Email: ", 20);
        campoEmail.setPreferredSize(fieldDimension);

        campoDataNascimento = new JTextFieldComTextoFixoArredondado("Nascimento: ", 10);
        campoDataNascimento.setPreferredSize(fieldDimension);

        campoEndereco = new JTextFieldComTextoFixoArredondado("Endereco: ", 20);
        campoEndereco.setPreferredSize(fieldDimension);

        campoNumero = new JTextFieldComTextoFixoArredondado("N*: ", 5);
        campoNumero.setPreferredSize(fieldDimension);

        campoComplemento = new JTextFieldComTextoFixoArredondado("Complemento: ", 25);
        campoComplemento.setPreferredSize(fieldDimension);

        campoPais = new JTextFieldComTextoFixoArredondado("Pais: ", 10);
        campoPais.setPreferredSize(fieldDimension);

        campoEstado = new JTextFieldComTextoFixoArredondado("Estado: ", 10);
        campoEstado.setPreferredSize(fieldDimension);

        campoMunicipio = new JTextFieldComTextoFixoArredondado("Municipio: ", 15);
        campoMunicipio.setPreferredSize(fieldDimension);

        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        // Configuração da fonte e cores para cada campo
        JTextFieldComTextoFixoArredondado[] campos = {campoNome, campoCPF, campoRG, campoTelefone, campoEmail, campoDataNascimento, campoEndereco, campoNumero, campoComplemento, campoPais, campoEstado, campoMunicipio};
        for (JTextFieldComTextoFixoArredondado campo : campos) {
            campo.setFont(font);
            campo.setForeground(Color.DARK_GRAY); // Texto digitado ficará em DARK_GRAY
//            campo.setPromptForeground(Cor.CINZA_CLARO.darker()); // Mantém o prompt em CINZA_CLARO
        }

        // Configuração do GroupLayout para alinhar os campos à esquerda e adicionar espaçamento
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
                        .addGap(70) // Aumentar o espaço entre Identificação e Localização
                        .addComponent(campoEndereco)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoComplemento)
                                .addComponent(campoNumero))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(campoPais)
                                .addComponent(campoEstado)
                                .addComponent(campoMunicipio))
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
                .addGap(30) // Espaço entre seções
                .addComponent(campoEndereco)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoComplemento)
                        .addComponent(campoNumero))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(campoPais)
                        .addComponent(campoEstado)
                        .addComponent(campoMunicipio))
        );

        add(camposPanel, BorderLayout.CENTER);

        JPanel botaoPanel = new JPanel();
        botaoPanel.setPreferredSize(new Dimension(600, 50));
        botaoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botaoPanel.add(btnSalvar);

        btnSalvar.addActionListener(e -> salvarIdentificacao());

        add(botaoPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void salvarIdentificacao() {
        // Obtenha os valores dos campos de Identificação de Pessoa e Localização
        String nome = campoNome.getText().replace("Nome: ", "").trim().toUpperCase();
        String cpf = campoCPF.getText().replace("CPF: ", "").trim();
        String rg = campoRG.getText().replace("RG: ", "").trim();
        String telefone = campoTelefone.getText().replace("Fone: ", "").trim();
        String email = campoEmail.getText().replace("Email: ", "").trim();
        String dataNascimento = campoDataNascimento.getText().replace("Nascimento: ", "").trim();

        String endereco = campoEndereco.getText().replace("Endereco: ", "").trim();
        String numero = campoNumero.getText().replace("N*: ", "").trim();
        String complemento = campoComplemento.getText().replace("Complemento: ", "").trim();
        String pais = campoPais.getText().replace("Pais: ", "").trim();
        String estado = campoEstado.getText().replace("Estado: ", "").trim();
        String municipio = campoMunicipio.getText().replace("Municipio: ", "").trim();

        // Lógica para salvar a identificação (ex: banco de dados ou outro processamento)
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + cpf);
        System.out.println("RG: " + rg);
        System.out.println("Telefone: " + telefone);
        System.out.println("Email: " + email);
        System.out.println("Data de Nascimento: " + dataNascimento);
        System.out.println("Endereço: " + endereco);
        System.out.println("Número: " + numero);
        System.out.println("Complemento: " + complemento);
        System.out.println("País: " + pais);
        System.out.println("Estado: " + estado);
        System.out.println("Município: " + municipio);

        JOptionPane.showMessageDialog(this, "Identificação salva com sucesso!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IdentificacaoPessoaFrame::new);
    }
}
