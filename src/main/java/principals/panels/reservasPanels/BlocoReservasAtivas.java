package principals.panels.reservasPanels;

import principals.tools.BotaoArredondado;
import principals.tools.Cor;
import principals.tools.Icones;
import request.BuscaReservasResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

import static principals.tools.Tool.resizeIcon;

public class BlocoReservasAtivas {

    public JPanel blocoReservasPanel(JPanel statusPanel, BuscaReservasResponse response){
        statusPanel.setBackground(Color.white);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tituloStatus = new JLabel(response.data().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tituloStatus.setForeground(Color.white);
        tituloStatus.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0xF5841B).darker());
        headerPanel.add(tituloStatus, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel reservasPanel = new JPanel();
        reservasPanel.setLayout(new GridLayout(0, 2, 10, 10));
        reservasPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        response.reservas().forEach(reserva -> {
            BotaoArredondado reservaButton = new BotaoArredondado("");
            reservaButton.setLayout(null);
            reservaButton.setPreferredSize(new Dimension(810, 95));
            reservaButton.setBackground(Color.WHITE);
            reservaButton.setBorderPainted(false);
            reservaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            reservaButton.addActionListener(e -> {});

            BotaoArredondado botaoQuarto = new BotaoArredondado(reserva.quarto() < 10L ? "0" + reserva.quarto() : reserva.quarto().toString());
            botaoQuarto.setLayout(null);
            botaoQuarto.setPreferredSize(new Dimension(60, 40));
            botaoQuarto.setBackground(new Color(0xF5841B).darker());
            botaoQuarto.setForeground(Color.WHITE);
            botaoQuarto.setFont(new Font("Inter", Font.BOLD, 40));
            botaoQuarto.setBounds(10, 10, 80, 75);
            reservaButton.add(botaoQuarto);

            JPanel blocoInfoPanelSuperior = new JPanel(new BorderLayout());
            blocoInfoPanelSuperior.setBackground(Color.WHITE);
            blocoInfoPanelSuperior.setBounds(100, 5, 695, 37);
            blocoInfoPanelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 5));
            reservaButton.add(blocoInfoPanelSuperior);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);

            ImageIcon iconeCalendario = resizeIcon(Icones.calendario, 20, 20);
            JLabel labelCalendario = new JLabel(iconeCalendario);
            labelCalendario.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            JLabel labelDataEntrada = new JLabel(reserva.data_entrada());
            labelDataEntrada.setToolTipText("Data de entrada");
            labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
            labelDataEntrada.setForeground(Cor.CINZA_ESCURO);
            labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

            JLabel labelDataSaida = new JLabel(reserva.data_saida());
            labelDataSaida.setToolTipText("Data de saida");
            labelDataSaida.setForeground(Cor.CINZA_ESCURO);
            labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));

            leftPanel.add(labelCalendario);
            leftPanel.add(labelDataEntrada);
            leftPanel.add(labelDataSaida);

            blocoInfoPanelSuperior.add(leftPanel, BorderLayout.WEST);

            reservasPanel.add(reservaButton);

            reservaButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    reservaButton.setBackground(Cor.CINZA_CLARO);
                    blocoInfoPanelSuperior.setBackground(Cor.CINZA_CLARO);
                    tituloStatus.setBackground(Cor.CINZA_CLARO);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    reservaButton.setBackground(Cor.BRANCO);
                    blocoInfoPanelSuperior.setBackground(Cor.BRANCO);
                    tituloStatus.setForeground(Cor.BRANCO);
                }
            });



        });

        statusPanel.add(reservasPanel, BorderLayout.CENTER);
        return statusPanel;
    }
}