package principals.panels.quartosPanel;

import principals.tools.JComboBoxArredondado;
import principals.tools.JTextFieldComTextoFixoArredondado;
import repository.QuartosRepository;

import javax.swing.*;
import java.awt.*;

public class AdicionarQuartoFrame extends JFrame {

    Font font = new Font("Segoe UI", Font.PLAIN, 16);

    private final JComboBoxArredondado<String> categoria = new JComboBoxArredondado<>();
    JTextFieldComTextoFixoArredondado numero;
    JTextFieldComTextoFixoArredondado qtd_cama_casal;
    JTextFieldComTextoFixoArredondado qtd_cama_solteiro;
    JTextFieldComTextoFixoArredondado qtd_rede;
    JTextFieldComTextoFixoArredondado qtd_beliche;

    public AdicionarQuartoFrame(QuartosRepository quartosRepository, String tituloQuarto) {
        setTitle(tituloQuarto);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(340, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(700, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));

        JLabel titulo = new JLabel(tituloQuarto);
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel centralPanel = new JPanel();
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        numero = new JTextFieldComTextoFixoArredondado("Numero: ", 3);
        qtd_cama_casal = new JTextFieldComTextoFixoArredondado("Qtd. camas casal: ", 3);
        qtd_cama_solteiro = new JTextFieldComTextoFixoArredondado("Qtd. camas solteiro: ", 3);
        qtd_rede = new JTextFieldComTextoFixoArredondado("Qtd. redes: ", 3);
        qtd_beliche = new JTextFieldComTextoFixoArredondado("Qtd. beliches: ", 3);

        JTextFieldComTextoFixoArredondado[] campos = {numero, qtd_cama_casal, qtd_cama_solteiro, qtd_rede, qtd_beliche};
        int y = 0;
        for (JTextFieldComTextoFixoArredondado campo : campos) {
            campo.setFont(font);
            campo.setForeground(Color.DARK_GRAY);
            gbc.gridx = 0;
            gbc.gridy = y;
            gbc.gridwidth = 2;
            centralPanel.add(campo, gbc);
            y++;
        }

        JPanel categoriaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        categoriaPanel.setBackground(Color.WHITE);

        JLabel lblCategoria = new JLabel("Categoria:");
        lblCategoria.setFont(font);
        lblCategoria.setForeground(Color.DARK_GRAY);

        categoria.setPreferredSize(new Dimension(200, 25));
        quartosRepository.listarCategorias().forEach(newCategoria -> categoria.addItem(newCategoria.descricao()));

        categoriaPanel.add(lblCategoria);
        categoriaPanel.add(categoria);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        centralPanel.add(categoriaPanel, gbc);

        add(centralPanel, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel();
        botoesPanel.setBackground(Color.WHITE);
        botoesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botoesPanel.add(btnSalvar);

        add(botoesPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}
