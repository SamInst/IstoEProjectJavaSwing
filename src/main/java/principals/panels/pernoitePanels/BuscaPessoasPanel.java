package principals.panels.pernoitePanels;

import principals.tools.CorPersonalizada;
import repository.PessoaRepository;
import request.BuscaPessoaRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BuscaPessoasPanel extends JPanel {
    PessoaRepository pessoaRepository = new PessoaRepository();
    private JTextField campoBusca;
    private JList<String> listaSugestoes;
    private DefaultListModel<String> sugestoesModel;
    private JPanel blocoPessoasAdicionadas;
    private List<Long> pessoasSelecionadas = new ArrayList<>();

    public BuscaPessoasPanel(List<Long> pessoas) {
        setLayout(new BorderLayout());
        setBackground(Color.RED);

        // Campo de busca de pessoas
        JPanel painelBusca = new JPanel();
        painelBusca.setLayout(new BorderLayout());

        campoBusca = new JTextField("Buscar pessoas por nome, cpf ou ID");
        campoBusca.setForeground(CorPersonalizada.CINZA_ESCURO);
        campoBusca.setFont(new Font("Roboto", Font.BOLD, 12));
        campoBusca.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        campoBusca.setPreferredSize(new Dimension(400, 30));
        campoBusca.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campoBusca.getText().equals("Buscar pessoas por nome, cpf ou ID")) {
                    campoBusca.setText("");  // Limpar o campo quando ganhar foco
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campoBusca.getText().isEmpty()) {
                    campoBusca.setText("Buscar pessoas por nome, cpf ou ID");  // Restaurar o placeholder se estiver vazio
                }
            }
        });

        campoBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarPessoas(campoBusca.getText());
            }
        });

        painelBusca.add(campoBusca, BorderLayout.NORTH);

        // Lista de sugestões
        sugestoesModel = new DefaultListModel<>();
        listaSugestoes = new JList<>(sugestoesModel);
        listaSugestoes.setVisible(false);  // Inicialmente oculta
        listaSugestoes.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));  // Adicionar um pequeno padding

        listaSugestoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String pessoaSelecionada = listaSugestoes.getSelectedValue();
                if (pessoaSelecionada != null) {
                    adicionarPessoaSelecionada(pessoaSelecionada, pessoas);
                    listaSugestoes.setVisible(false);  // Esconder a lista de sugestões após a seleção
                    campoBusca.setText("");  // Limpar o campo de busca após a seleção
                }
            }
        });

        JScrollPane scrollSugestoes = new JScrollPane(listaSugestoes);
        scrollSugestoes.setPreferredSize(new Dimension(150, 50));
        painelBusca.add(scrollSugestoes, BorderLayout.CENTER);

        add(painelBusca, BorderLayout.NORTH);

        // Bloco para exibir as pessoas adicionadas
        blocoPessoasAdicionadas = criarBlocoPessoasAdicionadas();
        add(blocoPessoasAdicionadas, BorderLayout.CENTER);
    }

    // Método que busca as pessoas no banco de dados
    private void buscarPessoas(String texto) {
        // Chamando o repositório para buscar as pessoas
        List<BuscaPessoaRequest> pessoas = pessoaRepository.buscarPessoaPorIdNomeOuCpf(texto);

        // Limpando as sugestões anteriores
        sugestoesModel.clear();
        if (pessoas.isEmpty()) {
            listaSugestoes.setVisible(false); // Esconde a lista se não houver resultados
        } else {
            listaSugestoes.setVisible(true); // Exibe a lista se houver resultados
            for (BuscaPessoaRequest pessoa : pessoas) {
                String resultado = "#" + pessoa.id() + " - " + pessoa.cpf() + "  " + pessoa.nome();
                sugestoesModel.addElement(resultado);
            }
        }
    }

    private void adicionarPessoaSelecionada(String pessoaSelecionada, List<Long> pessoas) {
        Long pessoaId = Long.valueOf(pessoaSelecionada.split(" - ")[0].replace("#", ""));

        if (pessoasSelecionadas.contains(pessoaId)) {
            JOptionPane.showMessageDialog(this, "Pessoa já foi adicionada.");
            return;
        }

        pessoasSelecionadas.add(pessoaId);
        pessoas.add(pessoaId);

        JLabel labelPessoa = new JLabel(pessoaSelecionada);
        labelPessoa.setForeground(CorPersonalizada.CINZA_ESCURO);
        labelPessoa.setFont(new Font("Roboto", Font.BOLD, 13));

        JButton botaoRemover = new JButton(new ImageIcon(new ImageIcon("src/main/resources/icons/remove.png")
                .getImage()
                .getScaledInstance(20, 20, Image.SCALE_SMOOTH))); // Ajuste do ícone
        botaoRemover.setPreferredSize(new Dimension(30, 30)); // Tamanho do botão
        botaoRemover.setBorderPainted(false);
        botaoRemover.setContentAreaFilled(false);
        botaoRemover.setFocusPainted(false);
        botaoRemover.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Cria um painel para organizar a pessoas e o botão remover
        JPanel painelPessoa = new JPanel(new BorderLayout()); // Altera para BorderLayout
        painelPessoa.add(labelPessoa, BorderLayout.CENTER); // Nome da pessoas no centro
        painelPessoa.add(botaoRemover, BorderLayout.EAST);  // Botão remover à direita

        // Ação ao clicar no botão remover
        botaoRemover.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Deseja remover " + pessoaSelecionada + "?",
                    "Remover Pessoa",
                    JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                removerPessoaSelecionada(pessoaId, painelPessoa, pessoas);
            }
        });

        // Adiciona o painel ao bloco
        blocoPessoasAdicionadas.add(painelPessoa);
        blocoPessoasAdicionadas.revalidate();
        blocoPessoasAdicionadas.repaint();
    }


    private void removerPessoaSelecionada(Long pessoaId, JPanel painelPessoa, List<Long> pessoas) {
        pessoasSelecionadas.remove(pessoaId);
        pessoas.remove(pessoaId);

        // Remove o painel da Robotoface
        blocoPessoasAdicionadas.remove(painelPessoa);
        blocoPessoasAdicionadas.revalidate();
        blocoPessoasAdicionadas.repaint();
    }


    // Bloco onde as pessoas selecionadas serão adicionadas
    // Bloco onde as pessoas selecionadas serão adicionadas
    private JPanel criarBlocoPessoasAdicionadas() {
        JPanel bloco = new JPanel();
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS)); // Usar Y_AXIS para adicionar verticalmente uma abaixo da outra
        bloco.setBackground(Color.LIGHT_GRAY);
        bloco.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding ao redor do bloco
        bloco.setAlignmentX(Component.LEFT_ALIGNMENT); // Garantir que o alinhamento seja à esquerda

        return bloco;
    }


}
