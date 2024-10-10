package principals.panels.pernoitesSubPanels;

import principals.tools.*;
import response.DiariaResponse;
import response.PernoiteResponse;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static principals.tools.Tool.resizeIcon;

public class BuscaPernoiteIndividual {

    public JPanel blocoBranco(JPanel blocoBranco, PernoiteResponse pernoite) {
        blocoBranco.setPreferredSize(new Dimension(500, 100));
        blocoBranco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        blocoBranco.setBackground(Color.WHITE);
        blocoBranco.setLayout(new BorderLayout());

        JPanel subBlocoEsquerdo = new JPanel();
        subBlocoEsquerdo.setPreferredSize(new Dimension(110, 100));
        subBlocoEsquerdo.setBackground(Color.WHITE);
        subBlocoEsquerdo.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        blocoBranco.add(subBlocoEsquerdo, BorderLayout.WEST);

        BotaoArredondado botaoQuarto = new BotaoArredondado(pernoite.quarto().toString());
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
        JLabel valorPago = new JLabel("R$ 100,00");
        valorPago.setFont(new Font("Inter", Font.BOLD, 20));
        valorPago.setForeground(new Color(0xF5841B));

        JLabel pago = new JLabel("Pago: " + valorPago.getText());
        pago.setFont(new Font("Inter", Font.BOLD, 20));


        JLabel totalPago = new JLabel("R$ " + pernoite.valor_total().toString());
        totalPago.setFont(new Font("Inter", Font.BOLD, 20));
        totalPago.setForeground(Cor.VERDE_ESCURO);

        JLabel total = new JLabel("Total: " + totalPago.getText());
        total.setFont(new Font("Inter", Font.BOLD, 20));

        subBlocoDireitoInferior.add(pago, BorderLayout.WEST);
        subBlocoDireitoInferior.add(total, BorderLayout.EAST);

        subBlocoDireito.add(subBlocoDireitoInferior);

        return blocoBranco;
    }


    public JPanel blocoVisualizarDiarias(JPanel blocoVisualizaDiarias, PernoiteResponse pernoite, DiariaResponse diaria){

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

        JLabel numeroDiaria = new JLabel(diaria.numero().toString());
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

        LabelArredondado labelDataEntrada = new LabelArredondado(diaria.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        labelDataEntrada.setToolTipText("Data de entrada");
        labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataEntrada.setForeground(Cor.CINZA_ESCURO);
        labelDataEntrada.setOpaque(false);
        labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

//        LabelArredondado labelDataSaida = new LabelArredondado(diaria.data_saida().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//        labelDataSaida.setToolTipText("Data de saida");
//        labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));
//        labelDataSaida.setForeground(Cor.CINZA_ESCURO);
//        labelDataSaida.setOpaque(false);
//        labelDataSaida.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        JLabel valorDiaria = new JLabel("             valor diária: R$" + diaria.valor_diaria());
        valorDiaria.setFont(new Font("Inter", Font.BOLD, 20));
        valorDiaria.setForeground(new Color(0xF5841B));

        blocoVisualizaDiarias.add(labelDiariaIcone);
        blocoVisualizaDiarias.add(labelEsquerdaIcone);
        blocoVisualizaDiarias.add(numeroDiaria);
        blocoVisualizaDiarias.add(labelDireitaIcone);
        blocoVisualizaDiarias.add(labelDataEntrada);
//        blocoVisualizaDiarias.add(labelDataSaida);
        blocoVisualizaDiarias.add(valorDiaria);

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

    public JPanel espacoBranco(JPanel blocoLaranja){
        blocoLaranja.setPreferredSize(new Dimension(500, 20));
        blocoLaranja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        blocoLaranja.setBackground(Color.WHITE);
        return blocoLaranja;
    }

    public JPanel blocoBotoesOpcoes(JPanel blocoOpcoes){
        blocoOpcoes.setPreferredSize(new Dimension(500, 40));
        blocoOpcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        blocoOpcoes.setBackground(Color.GREEN);
        blocoOpcoes.setLayout(new BorderLayout());
        blocoOpcoes.setBorder(BorderFactory.createEmptyBorder(5,5,5,10));

        JButton cancelar = new JButton("Cancelar");
        cancelar.setFocusPainted(false);
        cancelar.setPreferredSize(new Dimension(100, 30));
        cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelar.setBackground(Color.RED);
        blocoOpcoes.add(cancelar, BorderLayout.EAST);

        JButton finalizar = new JButton("Finalizar");
        finalizar.setFocusPainted(false);
        finalizar.setPreferredSize(new Dimension(100, 30));
        finalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        blocoOpcoes.add(cancelar, BorderLayout.WEST);
        blocoOpcoes.add(finalizar, BorderLayout.EAST);
        return blocoOpcoes;
    }

    public JPanel blocoPessoas(JPanel blocoPessoas){
        blocoPessoas.setPreferredSize(new Dimension(500, 45));
        blocoPessoas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        blocoPessoas.setBackground(Cor.AZUL_ESCURO);
        blocoPessoas.setLayout(new BorderLayout());
        blocoPessoas.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        ImageIcon iconePessoas = resizeIcon(Icones.pessoas_branco, 25, 25);
        JLabel labelPessoasIcone = new JLabel(iconePessoas);

        JLabel labelPessoas = new JLabel(" Pessoas");
        labelPessoas.setFont(new Font("Inter", Font.BOLD, 20));
        labelPessoas.setForeground(Color.WHITE);

        PanelArredondado panelPessoas = new PanelArredondado();
        panelPessoas.add(labelPessoasIcone);
        panelPessoas.setBackground(Cor.AZUL_ESCURO);
        panelPessoas.add(labelPessoas);

        blocoPessoas.add(panelPessoas, BorderLayout.WEST);


        JButton adicionarPessoas = new JButton("Adicionar");
        adicionarPessoas.setFocusPainted(false);
        adicionarPessoas.setPreferredSize(new Dimension(100, 30));
        adicionarPessoas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoPessoas.add(adicionarPessoas, BorderLayout.EAST);

        return blocoPessoas;

    }

    public JPanel blocoListaDePessoas(JPanel blocoListaPessoas, DiariaResponse diaria) {
        blocoListaPessoas.setLayout(new BoxLayout(blocoListaPessoas, BoxLayout.Y_AXIS));
        blocoListaPessoas.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));  // Margem ao redor do bloco todo
        blocoListaPessoas.setBackground(Color.WHITE);

        diaria.pessoas().forEach(pessoa -> {
            ImageIcon iconePessoas = resizeIcon(Icones.usuario, 25, 25);
            JLabel pessoaIcone = new JLabel(iconePessoas);

            PanelArredondado pessoaPanel = new PanelArredondado();
            pessoaPanel.setLayout(new BorderLayout());
            pessoaPanel.setPreferredSize(new Dimension(545, 40));  // Tamanho fixo para os paineis de pessoas
            pessoaPanel.setMaximumSize(new Dimension(545, 40));
            pessoaPanel.setMinimumSize(new Dimension(545, 40));

            JPanel pessoaInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pessoaInfoPanel.setOpaque(false);
            pessoaInfoPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

            JLabel id = new JLabel(" #" + pessoa.pessoa_id());
            id.setFont(new Font("Inter", Font.BOLD, 15));
            id.setForeground(Color.RED);

            JLabel nome = new JLabel(pessoa.nome() + "  ");
            nome.setFont(new Font("Inter", Font.BOLD, 15));
            nome.setForeground(Cor.CINZA_ESCURO);

            pessoaInfoPanel.add(pessoaIcone);
            pessoaInfoPanel.add(id);
            pessoaInfoPanel.add(nome);

            JLabel telefone = new JLabel(pessoa.telefone());
            telefone.setFont(new Font("Inter", Font.BOLD, 15));
            telefone.setForeground(Cor.CINZA_ESCURO);
            telefone.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

            pessoaPanel.add(pessoaInfoPanel, BorderLayout.WEST);
            pessoaPanel.add(telefone, BorderLayout.EAST);

            // Adiciona o painel de cada pessoa
            blocoListaPessoas.add(pessoaPanel);

            // Adiciona um espaço rígido de 10 pixels entre os painéis
            blocoListaPessoas.add(Box.createRigidArea(new Dimension(0, 3)));
        });

        blocoListaPessoas.revalidate();
        blocoListaPessoas.repaint();
        return blocoListaPessoas;
    }




    public JPanel blocoListaDePagamentos(JPanel blocoListaDePagamentos, DiariaResponse diaria){
        blocoListaDePagamentos.setLayout(new FlowLayout(FlowLayout.LEFT));
        blocoListaDePagamentos.setPreferredSize(new Dimension(500, 0));
//        blocoListaDePagamentos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoListaDePagamentos.setBackground(Color.WHITE);


        diaria.pagamento().forEach(pagamento -> {
            ImageIcon iconePagamento = resizeIcon(Icones.usuario, 25, 25);
            JLabel pagamentoIcone = new JLabel(iconePagamento);

            JPanel pessoaPanel = new JPanel();
            pessoaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            pessoaPanel.setPreferredSize(new Dimension(550, 40));

            JLabel id_pagamento = new JLabel(" # " + pagamento.pagamento_id());
            id_pagamento.setFont(new Font("Inter", Font.BOLD, 15));
            id_pagamento.setForeground(Color.RED);
            JLabel tipoPagamento = new JLabel(pagamento.tipo_pagamento() + "  ");
            tipoPagamento.setFont(new Font("Inter", Font.BOLD, 15));
            JLabel valor = new JLabel("R$ " + pagamento.valor_pagamento());
            valor.setFont(new Font("Inter", Font.BOLD, 15));

            pessoaPanel.add(pagamentoIcone);
            pessoaPanel.add(id_pagamento);
            pessoaPanel.add(tipoPagamento);
            pessoaPanel.add(valor, FlowLayout.RIGHT);

            blocoListaDePagamentos.add(pessoaPanel, BorderLayout.WEST);
        });

        blocoListaDePagamentos.revalidate();
        blocoListaDePagamentos.repaint();
        return blocoListaDePagamentos;
    }



    public JPanel blocoPagamentos(JPanel blocoPagamento){
        blocoPagamento.setPreferredSize(new Dimension(500, 45));
        blocoPagamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        blocoPagamento.setBackground(Cor.AZUL_ESCURO);
        blocoPagamento.setLayout(new BorderLayout());
        blocoPagamento.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        ImageIcon iconePessoas = resizeIcon(Icones.pagamento, 25, 25);
        JLabel labelPessoasIcone = new JLabel(iconePessoas);

        JLabel labelPessoas = new JLabel(" Pagamentos");
        labelPessoas.setFont(new Font("Inter", Font.BOLD, 20));
        labelPessoas.setForeground(Color.WHITE);

        PanelArredondado panelPessoas = new PanelArredondado();
        panelPessoas.add(labelPessoasIcone);
        panelPessoas.setBackground(Cor.AZUL_ESCURO);
        panelPessoas.add(labelPessoas);

        blocoPagamento.add(panelPessoas, BorderLayout.WEST);

        JButton adicionarPagamento = new JButton("Adicionar");
        adicionarPagamento.setFocusPainted(false);
        adicionarPagamento.setPreferredSize(new Dimension(100, 30));
        adicionarPagamento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoPagamento.add(adicionarPagamento, BorderLayout.EAST);

        return blocoPagamento;

    }


    public JPanel blocoConsumo(JPanel blocoConsumo, DiariaResponse.Consumo consumo){
        blocoConsumo.setPreferredSize(new Dimension(500, 45));
        blocoConsumo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        blocoConsumo.setBackground(Cor.AZUL_ESCURO);
        blocoConsumo.setLayout(new BorderLayout());
        blocoConsumo.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        ImageIcon iconeConsumo = resizeIcon(Icones.sacola_branca, 25, 25);
        JLabel consumoIcone = new JLabel(iconeConsumo);

        JLabel labelConsumo = new JLabel(" Consumo");
        labelConsumo.setFont(new Font("Inter", Font.BOLD, 20));
        labelConsumo.setForeground(Color.WHITE);

        JLabel labelTotalConsumo = new JLabel(" R$ "+ consumo.total_consumo());
        labelTotalConsumo.setFont(new Font("Inter", Font.BOLD, 20));
        labelTotalConsumo.setForeground(Color.WHITE);

        PanelArredondado panelConsumo = new PanelArredondado();
        panelConsumo.add(consumoIcone);
        panelConsumo.setBackground(Cor.AZUL_ESCURO);
        panelConsumo.add(labelTotalConsumo, BorderLayout.EAST);



        blocoConsumo.add(panelConsumo, BorderLayout.WEST);

        JButton adicionarConsumo = new JButton("Adicionar");
        adicionarConsumo.setFocusPainted(false);
        adicionarConsumo.setPreferredSize(new Dimension(100, 30));
        adicionarConsumo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoConsumo.add(adicionarConsumo, BorderLayout.EAST);

        return blocoConsumo;

    }

    public JPanel blocoListaDeConsumos(JPanel blocoListaDeConsumos, DiariaResponse.Consumo consumos){
        blocoListaDeConsumos.setLayout(new FlowLayout(FlowLayout.LEFT));
        blocoListaDeConsumos.setPreferredSize(new Dimension(500, 0));
//        blocoListaDeConsumos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        blocoListaDeConsumos.setBackground(Color.WHITE);

        consumos.itens().forEach(item -> {
            ImageIcon iconeConsumo = resizeIcon(Icones.usuario, 25, 25);
            JLabel pagamentoIcone = new JLabel(iconeConsumo);

            JPanel consumoPanel = new JPanel();
            consumoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            consumoPanel.setPreferredSize(new Dimension(550, 40));

            JLabel itemID = new JLabel(" #" + item.item_id());
            itemID.setFont(new Font("Inter", Font.BOLD, 15));
            itemID.setForeground(Color.RED);

            JLabel itemLabel = new JLabel(item.item());
            itemLabel.setFont(new Font("Inter", Font.BOLD, 15));

            JLabel valor = new JLabel("R$ " + item.valor_item());
            valor.setFont(new Font("Inter", Font.BOLD, 15));

            consumoPanel.add(pagamentoIcone);
            consumoPanel.add(itemID);
            consumoPanel.add(itemLabel);
            consumoPanel.add(valor, FlowLayout.RIGHT);

            blocoListaDeConsumos.add(consumoPanel, BorderLayout.WEST);
        });


    blocoListaDeConsumos.revalidate();
    blocoListaDeConsumos.repaint();
        return blocoListaDeConsumos;
    }


    public JPanel linhaCinza(JPanel linhaCinza){
        linhaCinza.setPreferredSize(new Dimension(500, 3));
        linhaCinza.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        linhaCinza.setBackground(Cor.CINZA_CLARO);
        return linhaCinza;
    }




    public void buscaPernoiteIndividual(PernoiteResponse response) {
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

        JPanel blocoBranco = blocoBranco(new JPanel(), response);
        JPanel linhaCinza = linhaCinza(new JPanel());
        JPanel linhaCinza2 = linhaCinza(new JPanel());

        background.add(blocoBranco);
        background.add(linhaCinza);

        var diaria = response.diarias().get(1);

        JPanel blocoVisualizaDiarias = blocoVisualizarDiarias(new JPanel(), response, diaria);
        JPanel espacoBranco = espacoBranco(new JPanel());
        JPanel blocoPessoas = blocoPessoas(new JPanel());
        JPanel blocoListaDePessoas = blocoListaDePessoas(new JPanel(), diaria);
        JPanel blocoPagamentos = blocoPagamentos(new JPanel());
        JPanel blocoListaDePagamentos = blocoListaDePagamentos(new JPanel(), diaria);
        JPanel blocoConsumo = blocoConsumo(new JPanel(), diaria.consumo());
        JPanel blocoListaConsumo = blocoListaDeConsumos(new JPanel(), diaria.consumo());
        JPanel blocoOpcoes = blocoBotoesOpcoes(new JPanel());

        background.add(blocoVisualizaDiarias);
        background.add(linhaCinza2);
        background.add(espacoBranco);
        background.add(blocoPessoas);
        background.add(blocoListaDePessoas);
        background.add(blocoPagamentos);
        background.add(blocoListaDePagamentos);
        background.add(blocoConsumo);
        background.add(blocoListaConsumo);
        background.add(blocoOpcoes);

        janelaAdicionar.add(background, BorderLayout.CENTER);

        janelaAdicionar.revalidate();
        janelaAdicionar.repaint();
    }


}









