package principals.panels.reservasPanels;

import principals.tools.*;
import request.BuscaReservasResponse;
import response.PernoiteResponse;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import static principals.tools.Tool.resizeIcon;

public class BuscaReservaIndividual {
    JFrame janelaAdicionar = new JFrame("Reserva");

    public void buscaReservaIndividual(BuscaReservasResponse response) {

    }


    public JPanel blocoQuarto(JPanel blocoQuarto, PernoiteResponse pernoite) {
        blocoQuarto.setPreferredSize(new Dimension(500, 100));
        blocoQuarto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        blocoQuarto.setBackground(Color.WHITE);
        blocoQuarto.setLayout(new BorderLayout());

        JPanel subBlocoEsquerdo = new JPanel();
        subBlocoEsquerdo.setPreferredSize(new Dimension(110, 100));
        subBlocoEsquerdo.setBackground(Color.WHITE);
        subBlocoEsquerdo.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        blocoQuarto.add(subBlocoEsquerdo, BorderLayout.WEST);

        BotaoArredondado botaoQuarto = new BotaoArredondado(pernoite.quarto() < 10L ? "0" + pernoite.quarto() : pernoite.quarto().toString());
        botaoQuarto.setPreferredSize(new Dimension(80, 70));
        botaoQuarto.setBackground(new Color(66, 75, 152));
        botaoQuarto.setForeground(Color.WHITE);
        botaoQuarto.setFont(new Font("Inter", Font.BOLD, 40));
        subBlocoEsquerdo.add(botaoQuarto);

        JPanel subBlocoDireito = new JPanel();
        subBlocoDireito.setLayout(new GridLayout(2, 1));
        blocoQuarto.add(subBlocoDireito, BorderLayout.CENTER);

        JPanel subBlocoDireitoSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subBlocoDireitoSuperior.setBackground(Color.WHITE);
        subBlocoDireitoSuperior.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        subBlocoDireitoSuperior.setPreferredSize(new Dimension(250, 50));
        subBlocoDireito.add(subBlocoDireitoSuperior);

        ImageIcon iconeCalendario = resizeIcon(Icones.calendario, 20, 20);
        JLabel labelCalendario = new JLabel(iconeCalendario);
        labelCalendario.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        LabelArredondado labelDataEntradaArredondado = new LabelArredondado(pernoite.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        labelDataEntradaArredondado.setToolTipText("Data de entrada");
        labelDataEntradaArredondado.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataEntradaArredondado.setForeground(new Color(0xF5841B));
        labelDataEntradaArredondado.setOpaque(false);
        labelDataEntradaArredondado.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        LabelArredondado labelDataSaidaArredondado = new LabelArredondado(pernoite.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        labelDataSaidaArredondado.setToolTipText("Data de entrada");
        labelDataSaidaArredondado.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataSaidaArredondado.setForeground(new Color(0xF5841B));
        labelDataSaidaArredondado.setOpaque(false);
        labelDataSaidaArredondado.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        JLabel espacamento = new JLabel("                  ");

        subBlocoDireitoSuperior.add(labelCalendario);
        subBlocoDireitoSuperior.add(labelDataEntradaArredondado);
        subBlocoDireitoSuperior.add(labelDataSaidaArredondado);
        subBlocoDireitoSuperior.add(espacamento);

        JButton editar = new JButton("Editar");
        editar.setFocusPainted(false);

        subBlocoDireitoSuperior.add(editar);

        JPanel subBlocoDireitoInferior = new JPanel(new BorderLayout());
        subBlocoDireitoInferior.setBackground(Color.WHITE);
        subBlocoDireitoInferior.setPreferredSize(new Dimension(250, 50));
        subBlocoDireitoInferior.setBorder(BorderFactory.createEmptyBorder(0,5,0,20));

        //TODO: adicionar o valor pago
        JLabel valorPago = new JLabel("R$" + FormatarFloat.format(100F));
        valorPago.setFont(new Font("Inter", Font.BOLD, 15));
        valorPago.setForeground(new Color(0xF5841B));

        JLabel pago = new JLabel("Pago: " + valorPago.getText());
        pago.setFont(new Font("Inter", Font.BOLD, 15));


        JLabel totalPago = new JLabel("R$ " + FormatarFloat.format(pernoite.valor_total()));
        totalPago.setFont(new Font("Inter", Font.BOLD, 15));
        totalPago.setForeground(Cor.VERDE_ESCURO);

        JLabel total = new JLabel("Total: " + totalPago.getText());
        total.setFont(new Font("Inter", Font.BOLD, 15));

        subBlocoDireitoInferior.add(pago, BorderLayout.WEST);
        subBlocoDireitoInferior.add(total, BorderLayout.EAST);

        subBlocoDireito.add(subBlocoDireitoInferior);

        return blocoQuarto;
    }
}
