package principals.panels.pernoitesSubPanels;

import principals.tools.*;
import response.PernoiteResponse;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static principals.tools.Tool.resizeIcon;

public class BuscaPernoiteIndividual {
    public static void main(String[] args) {
        JFrame janelaAdicionar = new JFrame("Pernoite");
        janelaAdicionar.setLayout(new BorderLayout());
        janelaAdicionar.setSize(580, 800);
        janelaAdicionar.setMinimumSize(new Dimension(580, 600));
        janelaAdicionar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaAdicionar.setBackground(Color.RED);
        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);

        JPanel background = new JPanel();
        background.setBackground(Color.RED);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        JPanel blocoBranco = blocoBranco(new JPanel());
        JPanel linhaCinza = linhaCinza(new JPanel());
        JPanel linhaCinza2 = linhaCinza(new JPanel());
        JPanel blocoVisualizaDiarias = blocoVisualizarDiarias(new JPanel());
        JPanel espacoBranco = espacoBranco(new JPanel());
        JPanel blocoPessoas = blocoPessoas(new JPanel());
        JPanel blocoAzul = blocoAzul(new JPanel());

        background.add(blocoBranco);
        background.add(linhaCinza);
        background.add(blocoVisualizaDiarias);
        background.add(linhaCinza2);
        background.add(espacoBranco);
        background.add(blocoPessoas);
        background.add(blocoAzul);

        janelaAdicionar.add(background, BorderLayout.CENTER);

        janelaAdicionar.revalidate();
        janelaAdicionar.repaint();
    }

    public static JPanel blocoBranco(JPanel blocoBranco) {
        blocoBranco.setPreferredSize(new Dimension(500, 100));
        blocoBranco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        blocoBranco.setBackground(Color.WHITE);
        blocoBranco.setLayout(new BorderLayout());

        JPanel subBlocoEsquerdo = new JPanel();
        subBlocoEsquerdo.setPreferredSize(new Dimension(110, 100));
        subBlocoEsquerdo.setBackground(Color.WHITE);
        subBlocoEsquerdo.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        blocoBranco.add(subBlocoEsquerdo, BorderLayout.WEST);

        BotaoArredondado botaoQuarto = new BotaoArredondado("11");
        botaoQuarto.setPreferredSize(new Dimension(80, 70));
        botaoQuarto.setBackground(new Color(66, 75, 152));
        botaoQuarto.setForeground(Color.WHITE);
        botaoQuarto.setFont(new Font("Inter", Font.BOLD, 40));
        subBlocoEsquerdo.add(botaoQuarto);


        JPanel subBlocoDireito = new JPanel();
        subBlocoDireito.setLayout(new GridLayout(2, 1));
        blocoBranco.add(subBlocoDireito, BorderLayout.CENTER);

        JPanel subBlocoDireitoSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subBlocoDireitoSuperior.setBackground(Color.WHITE);
        subBlocoDireitoSuperior.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        subBlocoDireitoSuperior.setPreferredSize(new Dimension(250, 50));
        subBlocoDireito.add(subBlocoDireitoSuperior);

        ImageIcon iconeCalendario = resizeIcon(Icones.calendario, 20, 20);
        JLabel labelCalendario = new JLabel(iconeCalendario);
        labelCalendario.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        LabelArredondado labelDataEntradaArredondado = new LabelArredondado("22/22/2222");
        labelDataEntradaArredondado.setToolTipText("Data de entrada");
        labelDataEntradaArredondado.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataEntradaArredondado.setForeground(new Color(0xF5841B));
        labelDataEntradaArredondado.setOpaque(false);
        labelDataEntradaArredondado.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        LabelArredondado labelDataSaidaArredondado = new LabelArredondado("44/44/4444");
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

        JLabel valorPago = new JLabel("R$ 100,00");
        valorPago.setFont(new Font("Inter", Font.BOLD, 20));
        valorPago.setForeground(new Color(0xF5841B));

        JLabel pago = new JLabel("Pago: " + valorPago.getText());
        pago.setFont(new Font("Inter", Font.BOLD, 20));


        JLabel totalPago = new JLabel("R$ 300,00");
        totalPago.setFont(new Font("Inter", Font.BOLD, 20));
        totalPago.setForeground(Cor.VERDE_ESCURO);

        JLabel total = new JLabel("Total: " + totalPago.getText());
        total.setFont(new Font("Inter", Font.BOLD, 20));

        subBlocoDireitoInferior.add(pago, BorderLayout.WEST);
        subBlocoDireitoInferior.add(total, BorderLayout.EAST);

        subBlocoDireito.add(subBlocoDireitoInferior);

        return blocoBranco;
    }


    public static JPanel blocoVisualizarDiarias(JPanel blocoVisualizaDiarias){

        List<LocalDate> datas = new ArrayList<>();
        datas.add(LocalDate.of(2024, 10, 1));
        datas.add(LocalDate.of(2024, 10, 2));
        datas.add(LocalDate.of(2024, 10, 3));
        datas.add(LocalDate.of(2024, 10, 4));

        blocoVisualizaDiarias.setPreferredSize(new Dimension(500, 50));
        blocoVisualizaDiarias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        blocoVisualizaDiarias.setBackground(Color.WHITE);
        blocoVisualizaDiarias.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        blocoVisualizaDiarias.setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageIcon iconeDiaria = resizeIcon(Icones.diarias_quantidade, 25, 25);
        JLabel labelDiariaIcone = new JLabel(iconeDiaria);
        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        ImageIcon iconeEsquerda = resizeIcon(Icones.esquerda, 20, 20);
        JButton labelEsquerdaIcone = new JButton(iconeEsquerda);
        labelEsquerdaIcone.setOpaque(true);
        labelEsquerdaIcone.setBorderPainted(false);
        labelEsquerdaIcone.setBackground(Color.WHITE);
        labelEsquerdaIcone.setFocusPainted(false);
        labelEsquerdaIcone.setPreferredSize(new Dimension(30, 30));
        labelEsquerdaIcone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

        JLabel numeroDiaria = new JLabel("1");
        numeroDiaria.setFont(new Font("Inter", Font.BOLD, 20));

        ImageIcon iconeDireita = resizeIcon(Icones.direita, 20, 20);
        JButton labelDireitaIcone = new JButton(iconeDireita);
        labelDireitaIcone.setOpaque(true);
        labelDireitaIcone.setBorderPainted(false);
        labelDireitaIcone.setBackground(Color.WHITE);
        labelDireitaIcone.setFocusPainted(false);
        labelDireitaIcone.setPreferredSize(new Dimension(30, 30));
        labelDireitaIcone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        LabelArredondado labelDataEntrada = new LabelArredondado("22/22/2222");
        labelDataEntrada.setToolTipText("Data de entrada");
        labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));

        labelDataEntrada.setForeground(Cor.CINZA_ESCURO);
        labelDataEntrada.setOpaque(false);
        labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        LabelArredondado labelDataSaida = new LabelArredondado("33/33/3333");
        labelDataSaida.setToolTipText("Data de saida");
        labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataSaida.setForeground(Cor.CINZA_ESCURO);
        labelDataSaida.setOpaque(false);
        labelDataSaida.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));



        blocoVisualizaDiarias.add(labelDiariaIcone);
        blocoVisualizaDiarias.add(labelEsquerdaIcone);
        blocoVisualizaDiarias.add(numeroDiaria);
        blocoVisualizaDiarias.add(labelDireitaIcone);
        blocoVisualizaDiarias.add(labelDataEntrada);

        blocoVisualizaDiarias.add(labelDataSaida);

        AtomicInteger i = new AtomicInteger(1);  //TODO: pegar a diaria atual
        if (i.get() == 1) labelEsquerdaIcone.setEnabled(false);
        if (i.get() == datas.size() - 1) labelDireitaIcone.setEnabled(false);

        labelDireitaIcone.addActionListener(e -> {

            if (i.get() == datas.size() - 1){
                labelDireitaIcone.setEnabled(false);
                labelEsquerdaIcone.setEnabled(true);

            } else {
                labelEsquerdaIcone.setEnabled(true);
                i.getAndIncrement();

                if (i.get() == datas.size() - 1){
                    labelDireitaIcone.setEnabled(false);
                    labelEsquerdaIcone.setEnabled(true);
                    i.getAndDecrement();
                }
            }
        });

        labelEsquerdaIcone.addActionListener(e -> {
            if (i.get() == 0){
                labelDireitaIcone.setEnabled(true);
                labelEsquerdaIcone.setEnabled(false);

            } else {
                labelDireitaIcone.setEnabled(true);
                i.getAndDecrement();

                if (i.get() == 0){
                    labelDireitaIcone.setEnabled(true);
                    labelEsquerdaIcone.setEnabled(false);
                    i.getAndIncrement();
                }
            }

        });


        return blocoVisualizaDiarias;
    }

    public static JPanel espacoBranco(JPanel blocoLaranja){
        blocoLaranja.setPreferredSize(new Dimension(500, 20));
        blocoLaranja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        blocoLaranja.setBackground(Color.WHITE);
        return blocoLaranja;
    }

    public static JPanel blocoPessoas(JPanel blocoPessoas){
        blocoPessoas.setPreferredSize(new Dimension(500, 45));
        blocoPessoas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        blocoPessoas.setBackground(Cor.AZUL_ESCURO);
        blocoPessoas.setLayout(new BorderLayout());
        blocoPessoas.setBorder(BorderFactory.createEmptyBorder(3,5,0,10));

        ImageIcon iconePessoas = resizeIcon(Icones.pessoas_branco, 25, 25);
        JLabel labelPessoasIcone = new JLabel(iconePessoas);

        JLabel labelPessoas = new JLabel("Pessoas");
        labelPessoas.setFont(new Font("Inter", Font.BOLD, 20));
        labelPessoas.setForeground(Color.WHITE);

        PanelArredondado panelPessoas = new PanelArredondado();
        panelPessoas.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelPessoas.add(labelPessoasIcone);
        panelPessoas.setBackground(Cor.AZUL_ESCURO);
        panelPessoas.add(labelPessoas);
        blocoPessoas.add(panelPessoas, BorderLayout.WEST);

        return blocoPessoas;

    }

    public static JPanel blocoAzul(JPanel blocoAzul){
        blocoAzul.setPreferredSize(new Dimension(500, 70));
        blocoAzul.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoAzul.setBackground(Color.BLUE);
        return blocoAzul;
    }

    public static JPanel linhaCinza(JPanel linhaCinza){
        linhaCinza.setPreferredSize(new Dimension(500, 3));
        linhaCinza.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        linhaCinza.setBackground(Cor.CINZA_CLARO);
        return linhaCinza;
    }




    public static void buscaPernoiteIndividual(PernoiteResponse response) {

    }


}









