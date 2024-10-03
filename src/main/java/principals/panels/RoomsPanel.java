package principals.panels;

import principals.tools.Botoes;
import principals.tools.Tool;
import repository.PessoaRepository;
import response.PessoaResponse;

import javax.swing.*;
import java.awt.*;

public class RoomsPanel extends JPanel {

    public RoomsPanel() {
        setBackground(new Color(230, 230, 230));
        setLayout(new BorderLayout());

        // Botão Adicionar
        JButton btnAdicionar = Botoes.botaoEstilizado(
                "Adicionar Quarto",
                25,
                "src/main/resources/icons/plus_icon.png",
                40,
                40,
                new Color(76, 175, 80)
        );

        JButton btnAdicionar2 = Botoes.botaoEstilizado(
                "Pesquisar",
                25,
                "src/main/resources/icons/lupa.png",
                40,
                40,
                new Color(66, 75, 152)
        );

        // Painel para o botão no topo à direita
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20)); // 10px da borda direita e do topo
        topRightPanel.setOpaque(false); // Transparente para combinar com o fundo
        topRightPanel.setBackground(Color.BLUE);
        topRightPanel.add(btnAdicionar); // Adiciona o botão "Adicionar" no lado direito
        topRightPanel.add(btnAdicionar2);

        // Label Título à esquerda
        JLabel labelTitulo = new JLabel("Quartos", Tool.resizeIcon(Botoes.quartos_icon, 50, 50), JLabel.LEFT);
        labelTitulo.setFont(new Font("Inter", Font.BOLD, 30));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setOpaque(true);
        labelTitulo.setBackground(new Color(66, 75, 152));  // Cor de fundo
        labelTitulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Espaçamento interno
        labelTitulo.setIconTextGap(10);

        // Painel superior que vai conter o título à esquerda e os botões à direita
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Deixa o fundo transparente

        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margens ao redor do label
        topPanel.add(labelTitulo, BorderLayout.WEST); // Coloca o label no lado esquerdo
        topPanel.add(topRightPanel, BorderLayout.EAST); // Coloca o painel dos botões no lado direito

        // Adiciona o painel superior ao topo do layout principal
        add(topPanel, BorderLayout.NORTH);




        JPanel panel = new JPanel();
        topRightPanel.setOpaque(false); // Transparente para combinar com o fundo
        topRightPanel.setBackground(Color.RED);
        topRightPanel.add(btnAdicionar); // Adiciona o botão "Adicionar" no lado direito
        topRightPanel.add(btnAdicionar2);
        add(panel, BorderLayout.CENTER);

    }

    public PessoaResponse buscarPessoaPorID(Long id) {
        PessoaRepository pessoaRepository = new PessoaRepository();
        return pessoaRepository.buscarPessoaPorID(id);
    }
}


