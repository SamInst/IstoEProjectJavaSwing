package menu.panels.reservasPanel;

import buttons.ShadowButton;
import notifications.Notifications;
import request.BuscaReservasResponse;
import response.PessoaResponse;
import tools.BotaoArredondado;
import tools.ImagemArredodanda;
import tools.LabelArredondado;
import tools.SimpleDocumentListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static buttons.Botoes.btn_azul;
import static buttons.Botoes.btn_backgroung;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.TOP_CENTER;
import static tools.CorPersonalizada.*;
import static tools.Icones.*;
import static tools.Resize.resizeIcon;

public class PeoplePanel {
    private final ReservasPanel mainPanel;
    private final List<PessoaResponse> selectedPeople = new ArrayList<>();

    public PeoplePanel(ReservasPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void createGoogleStyleBuscaPessoaPanel(JPanel pessoasTab, BuscaReservasResponse reserva) {
        selectedPeople.clear();
        reserva.pessoas().forEach(r -> {
            PessoaResponse pessoa = mainPanel.getPessoaRepository().buscarPessoaPorID(r.pessoa_id());
            if (!selectedPeople.contains(pessoa)) {
                selectedPeople.add(pessoa);
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel buscarPessoaPanel = new JPanel(new BorderLayout());
        buscarPessoaPanel.setBackground(BACKGROUND_GRAY);

        JTextField buscarPessoaField = new JTextField(40);
        JPanel buscarPessoaInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscarPessoaInputPanel.setBackground(BACKGROUND_GRAY);
        buscarPessoaInputPanel.add(new JLabel(resizeIcon(search, 15, 15)));
        buscarPessoaInputPanel.add(new JLabel("Buscar Pessoa: "));
        buscarPessoaInputPanel.add(buscarPessoaField);
        buscarPessoaPanel.add(buscarPessoaInputPanel, BorderLayout.NORTH);
        container.add(buscarPessoaPanel, BorderLayout.NORTH);

        JPanel pessoasContainer = new JPanel();
        pessoasContainer.setLayout(new BoxLayout(pessoasContainer, BoxLayout.Y_AXIS));
        pessoasContainer.setOpaque(false);

        JScrollPane scrollPanePessoas = new JScrollPane(pessoasContainer);
        scrollPanePessoas.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPanePessoas.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanePessoas.setBorder(BorderFactory.createEmptyBorder());
        container.add(scrollPanePessoas, BorderLayout.CENTER);

        atualizarPainelPessoas(reserva, pessoasContainer);

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY));

        DefaultListModel<PessoaResponse> sugestaoModel = new DefaultListModel<>();
        JList<PessoaResponse> sugestaoList = new JList<>(sugestaoModel);
        sugestaoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sugestaoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PessoaResponse pessoa) {
                    setText(pessoa.nome());
                }
                return this;
            }
        });

        JScrollPane scrollPaneSugestoes = new JScrollPane(sugestaoList);
        scrollPaneSugestoes.setPreferredSize(new Dimension(buscarPessoaField.getPreferredSize().width, 150));
        popupMenu.add(scrollPaneSugestoes);

        buscarPessoaField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                String texto = buscarPessoaField.getText().trim();
                if (texto.length() >= 3) {
                    List<PessoaResponse> resultados = mainPanel.getPessoaRepository().buscarPessoaPorNome(texto);
                    List<Long> pessoasIds = reserva.pessoas().stream().map(BuscaReservasResponse.Pessoas::pessoa_id).toList();
                    resultados.removeIf(p -> pessoasIds.contains(p.id()));

                    sugestaoModel.clear();
                    if (!resultados.isEmpty()) {
                        resultados.forEach(sugestaoModel::addElement);
                        popupMenu.setFocusable(false);
                        popupMenu.show(buscarPessoaField, 0, buscarPessoaField.getHeight());
                        SwingUtilities.invokeLater(buscarPessoaField::requestFocusInWindow);
                    } else {
                        popupMenu.setVisible(false);
                    }
                } else {
                    popupMenu.setVisible(false);
                }
            }
        });

        sugestaoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    PessoaResponse pessoaSelecionada = sugestaoList.getSelectedValue();
                    if (pessoaSelecionada != null) {
                        mainPanel.getReservasRepository().adicionarPessoaReserva(reserva.reserva_id(), pessoaSelecionada.id());
                        notification(Notifications.Type.SUCCESS, TOP_CENTER, "Pessoa adicionada com sucesso! \n" + pessoaSelecionada.nome());

                        atualizarPainelPessoas(reserva, pessoasContainer);
                        mainPanel.refreshPanel();

                        sugestaoModel.clear();
                        popupMenu.setVisible(false);
                        buscarPessoaField.setText("");
                    }
                }
            }
        });

        pessoasTab.setLayout(new BorderLayout());
        pessoasTab.add(container, BorderLayout.CENTER);
        pessoasTab.revalidate();
        pessoasTab.repaint();
    }

    private void atualizarPainelPessoas(BuscaReservasResponse reserva, JPanel pessoasContainer) {
        pessoasContainer.removeAll();
        List<BuscaReservasResponse.Pessoas> atuais =
                mainPanel.getReservasRepository().buscarPessoasPorReserva(reserva.reserva_id());

        for (BuscaReservasResponse.Pessoas registro : atuais) {
            PessoaResponse pessoa = mainPanel.getPessoaRepository().buscarPessoaPorID(registro.pessoa_id());
            BotaoArredondado bloco =
                    adicionarBlocoPessoa(pessoa, registro, reserva, pessoasContainer);
            bloco.setAlignmentX(Component.LEFT_ALIGNMENT);
            pessoasContainer.add(bloco);
        }

        pessoasContainer.revalidate();
        pessoasContainer.repaint();

        mainPanel.atualizarContadores(reserva);
    }

    private BotaoArredondado adicionarBlocoPessoa(
            PessoaResponse pessoa,
            BuscaReservasResponse.Pessoas registro,
            BuscaReservasResponse reserva,
            JPanel pessoasContainer
    ) {
        BufferedImage foto = null;
        try { foto = mainPanel.getPessoaRepository().buscarFotoBufferedPessoaPorId(pessoa.id()); }
        catch (SQLException | IOException ignored) {}

        LabelArredondado labelFoto = new LabelArredondado("");
        labelFoto.setBackground(BACKGROUND_GRAY);
        ImageIcon icon = (foto != null)
                ? resizeIcon(new ImageIcon(ImagemArredodanda.arredondar(foto)), 50, 50)
                : resizeIcon(new ImageIcon(ImagemArredodanda.arredondar(
                ImagemArredodanda.convertImageIconToBufferedImage(
                        pessoa.sexo().equals(enums.GeneroEnum.FEMININO.ordinal()) ? user_sem_foto_feminino : user_sem_foto
                )
        )), 50, 50);
        labelFoto.setIcon(icon);

        BotaoArredondado bloco = new BotaoArredondado("");
        bloco.setBorderPainted(false);
        bloco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bloco.setLayout(new BorderLayout());
        bloco.setOpaque(false);
        bloco.setContentAreaFilled(false);
        bloco.setFocusPainted(false);
        bloco.setBackground(BACKGROUND_GRAY);
        bloco.setPreferredSize(new Dimension(0, 60));
        bloco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        JLabel lbl = new JLabel("<html>" + pessoa.nome() + "<br>" + pessoa.telefone() + "</html>");
        lbl.setForeground(GRAY);
        lbl.setFont(new Font("Roboto", Font.PLAIN, 14));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        center.add(lbl, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 15));
        btns.setOpaque(false);

        ShadowButton badge = btn_azul("Representante");
        badge.setPreferredSize(new Dimension(120, 30));
        badge.setFocusPainted(false);
        badge.enableHoverEffect();

        ShadowButton definir = btn_backgroung("Definir Representante");
        definir.setPreferredSize(new Dimension(150, 30));
        definir.setFocusPainted(false);
        definir.enableHoverEffect();

        btns.add(registro.representante() ? badge : definir);

        definir.addActionListener(e -> {
            try {
                List<BuscaReservasResponse.Pessoas> todos =
                        mainPanel.getReservasRepository().buscarPessoasPorReserva(reserva.reserva_id());
                for (var p : todos) {
                    if (p.representante() && p.pessoa_id() != pessoa.id()) {
                        mainPanel.getReservasRepository().definirRepresentanteDaReserva(
                                reserva.reserva_id(), p.pessoa_id(), false);
                    }
                }
                mainPanel.getReservasRepository().definirRepresentanteDaReserva(
                        reserva.reserva_id(), pessoa.id(), true);
                notification(Notifications.Type.SUCCESS, TOP_CENTER,
                        pessoa.nome() + " definido como representante!");

                atualizarPainelPessoas(reserva, pessoasContainer);
            } catch (Exception ex) {
                ex.printStackTrace();
                notification(Notifications.Type.ERROR, TOP_CENTER,
                        "Erro ao definir representante: " + ex.getMessage());
            }
            mainPanel.refreshPanel();
        });

        ShadowButton remove = btn_backgroung("");
        remove.setIcon(resizeIcon(close, 15, 15));
        remove.setPreferredSize(new Dimension(40, 30));
        remove.setFocusPainted(false);
        remove.enableHoverEffect();
        remove.addActionListener(e -> {
            mainPanel.getReservasRepository().removerPessoaReserva(pessoa.id(), reserva.reserva_id());
            notification(Notifications.Type.WARNING, TOP_CENTER,
                    "Pessoa removida: " + pessoa.nome());
            atualizarPainelPessoas(reserva, pessoasContainer);
            mainPanel.refreshPanel();
        });

        btns.add(remove);

        if (mainPanel.getReservasRepository().buscarPessoasPorReserva(reserva.reserva_id()).size() < 2) {
            remove.setEnabled(false);
        }

        bloco.add(labelFoto, BorderLayout.WEST);
        bloco.add(center, BorderLayout.CENTER);
        bloco.add(btns, BorderLayout.EAST);

        return bloco;
    }
}