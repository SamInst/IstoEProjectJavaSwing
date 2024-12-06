package principals.panels;

import enums.StatusPernoiteEnum;
import principals.panels.pernoitePanels.BlocosPernoitesAtivos;
import principals.tools.Botoes;
import principals.tools.Icones;
import principals.tools.Tool;
import repository.PessoaRepository;
import repository.QuartosRepository;
import response.PessoaResponse;

import javax.swing.*;
import java.awt.*;

import static principals.tools.Cor.VERDE_ESCURO;

public class RoomsPanel extends JPanel {
    JPanel quartosPanel = new JPanel();
    QuartosRepository quartosRepository = new QuartosRepository();

    public RoomsPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JPanel identificadorPanel = principals.Menu.createIdentificadorPanel("Apartamentos", Icones.quartos);
        identificadorPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        topPanel.add(identificadorPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttonPanel.setMinimumSize(new Dimension(20, 20));

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.setPreferredSize(new Dimension(125, 40));
        btnAdicionar.addActionListener(e -> {});

        buttonPanel.add(btnAdicionar);


        identificadorPanel.add(buttonPanel, BorderLayout.WEST);


        add(topPanel, BorderLayout.NORTH);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(Color.WHITE);
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < StatusPernoiteEnum.values().length; i++){
//            pernoitesPanel.add(new BlocosPernoitesAtivos().blocoPernoitesAtivos(new JPanel(), pernoitesRepository, StatusPernoiteEnum.values()[i]));
        }

        JScrollPane scrollPane = new JScrollPane(quartosPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

    }

}


