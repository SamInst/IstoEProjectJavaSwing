package menu.panels.quartosPanel;

import enums.StatusQuartoEnum;
import repository.QuartosRepository;
import request.AtualizarDadosQuartoRequest;
import tools.JComboBoxArredondado;
import tools.JTextFieldComTextoFixoArredondado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class AdicionarQuartoFrame extends JFrame {
    Font font = new Font("Roboto", Font.PLAIN, 16);

    private final JComboBoxArredondado<String> categoria = new JComboBoxArredondado<>();
    JTextFieldComTextoFixoArredondado numero = new JTextFieldComTextoFixoArredondado("* Numero: ", 3);
    JTextFieldComTextoFixoArredondado qtd_pessoas = new JTextFieldComTextoFixoArredondado("* Qtd. pessoas: ", 3);
    JTextFieldComTextoFixoArredondado qtd_cama_casal = new JTextFieldComTextoFixoArredondado("* Qtd. camas casal: ", 3);
    JTextFieldComTextoFixoArredondado qtd_cama_solteiro = new JTextFieldComTextoFixoArredondado("* Qtd. camas solteiro: ", 3);
    JTextFieldComTextoFixoArredondado qtd_rede = new JTextFieldComTextoFixoArredondado("* Qtd. redes: ", 3);
    JTextFieldComTextoFixoArredondado qtd_beliche = new JTextFieldComTextoFixoArredondado("* Qtd. beliches: ", 3);
    JTextFieldComTextoFixoArredondado descricao = new JTextFieldComTextoFixoArredondado("Descricao: ", 3);

    public AdicionarQuartoFrame(QuartosRepository quartosRepository, Long quarto_id, RoomsPanel roomsPanel) {
        setTitle("Quarto");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(340, 460);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        restringirEntradaNumerica(numero);
        restringirEntradaNumerica(qtd_pessoas);
        restringirEntradaNumerica(qtd_cama_casal);
        restringirEntradaNumerica(qtd_cama_solteiro);
        restringirEntradaNumerica(qtd_rede);
        restringirEntradaNumerica(qtd_beliche);

        JPanel tituloPanel = new JPanel();
        tituloPanel.setBackground(new Color(0x424B98));
        tituloPanel.setPreferredSize(new Dimension(700, 50));
        tituloPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 0));

        JLabel titulo = new JLabel("Identificacao Quarto");
        titulo.setFont(new Font("Roboto", Font.PLAIN, 20));
        titulo.setForeground(Color.WHITE);
        tituloPanel.add(titulo);
        add(tituloPanel, BorderLayout.NORTH);

        JPanel centralPanel = new JPanel();
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextFieldComTextoFixoArredondado[] campos = {numero, qtd_pessoas, qtd_cama_casal, qtd_cama_solteiro, qtd_rede, qtd_beliche, descricao};
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
        btnSalvar.setFont(new Font("Roboto", Font.BOLD, 16));
        btnSalvar.setBackground(new Color(0, 153, 0));
        btnSalvar.setForeground(Color.WHITE);
        botoesPanel.add(btnSalvar);

        btnSalvar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               salvarQuarto(quartosRepository, roomsPanel);
            }
        });

        add(botoesPanel, BorderLayout.SOUTH);
        setVisible(true);

        if (quarto_id != null) {
            preencherDadosQuartoExistente(quartosRepository, quarto_id, titulo);
        }
    }


    public void salvarQuarto(QuartosRepository quartosRepository, RoomsPanel roomsPanel) {
        if (numero.getText().replace("* Numero: ", "").trim().isEmpty()
                || qtd_pessoas.getText().replace("* Qtd. pessoas: ", "").trim().isEmpty()
                || qtd_cama_casal.getText().replace("* Qtd. camas casal: ", "").trim().isEmpty()
                || qtd_cama_solteiro.getText().replace("* Qtd. camas solteiro: ", "").trim().isEmpty()
                || qtd_rede.getText().replace("* Qtd. redes: ", "").trim().isEmpty()
                || qtd_beliche.getText().replace("* Qtd. beliches: ", "").trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Os campos obrigatórios (*) não podem ficar vazios!",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var numero_quarto = Long.parseLong(numero.getText().replace("* Numero: ", "").replaceAll("[^0-9]", ""));
        var cama_solteiro = Integer.parseInt(qtd_cama_solteiro.getText().replace("* Qtd. camas solteiro: ", "").replaceAll("[^0-9]", ""));
        var cama_casal = Integer.parseInt(qtd_cama_casal.getText().replace("* Qtd. camas casal: ", "").replaceAll("[^0-9]", ""));
        var rede = Integer.parseInt(qtd_rede.getText().replace("* Qtd. redes: ", "").replaceAll("[^0-9]", ""));
        var beliche = Integer.parseInt(qtd_beliche.getText().replace("* Qtd. beliches: ", "").replaceAll("[^0-9]", ""));
        var pessoas = Integer.parseInt(qtd_pessoas.getText().replace("* Qtd. pessoas: ", "").replaceAll("[^0-9]", ""));
        var quarto_descricao = descricao.getText().replace("Descricao: ", "").trim();

        var categoria_selecionada = quartosRepository.buscaCategoriaPorNome(
                Objects.requireNonNull(categoria.getSelectedItem()).toString());

        var dadosQuartoRequest = new AtualizarDadosQuartoRequest(
                numero_quarto,
                quarto_descricao,
                pessoas,
                cama_casal,
                cama_solteiro,
                beliche,
                rede,
                categoria_selecionada.id(),
                StatusQuartoEnum.DISPONIVEL
        );

        if (quartosRepository.verificaQuartoExistente(numero_quarto)){
            JOptionPane.showMessageDialog(this, "Quarto " + numero_quarto + " ja cadastrado!", "Erro", JOptionPane.WARNING_MESSAGE);
        } else {
            quartosRepository.salvarQuarto(dadosQuartoRequest);
            dispose();
            roomsPanel.refreshPanel();
            System.out.println(dadosQuartoRequest);
        }
    }

    private void restringirEntradaNumerica(JTextFieldComTextoFixoArredondado campo) {
        campo.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c)) {
                    evt.consume();
                }
            }
        });
    }

    private void preencherDadosQuartoExistente(QuartosRepository quartosRepository, Long quarto_id, JLabel titulo){
        var quarto = quartosRepository.buscaQuartoPorId(quarto_id);

        setTitle("Buscar quarto");
        titulo.setText(quarto_id < 10L ? " Quarto 0" + quarto_id : "Quarto " + quarto_id);

        numero.setText(String.valueOf(quarto.quarto_id()));
        qtd_pessoas.setText(String.valueOf(quarto.quantidade_pessoas()));
        qtd_cama_casal.setText(String.valueOf(quarto.qtd_cama_casal()));
        qtd_cama_solteiro.setText(String.valueOf(quarto.qtd_cama_solteiro()));
        qtd_rede.setText(String.valueOf(quarto.qtd_rede()));
        qtd_beliche.setText(String.valueOf(quarto.qtd_cama_beliche()));
        descricao.setText(quarto.descricao());
        categoria.setSelectedItem(quarto.categoria().categoria());
    }

}
