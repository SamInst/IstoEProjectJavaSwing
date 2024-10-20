package principals.panels.pernoitePanels;

import principals.tools.*;
import response.DiariaResponse;
import response.PernoiteResponse;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static principals.tools.Tool.resizeIcon;

public class BuscaPernoiteIndividual {
    JFrame janelaAdicionar = new JFrame("Pernoite");
    JButton labelEsquerdaIcone;
    JButton labelDireitaIcone;

    public void buscaPernoiteIndividual(PernoiteResponse response) {
        ImageIcon iconeEsquerda = resizeIcon(Icones.esquerda, 20, 20);
        labelEsquerdaIcone = new JButton(iconeEsquerda);
        labelEsquerdaIcone.setOpaque(true);
        labelEsquerdaIcone.setBorderPainted(false);
        labelEsquerdaIcone.setBackground(Cor.CINZA_CLARO);
        labelEsquerdaIcone.setFocusPainted(false);
        labelEsquerdaIcone.setPreferredSize(new Dimension(30, 30));
        labelEsquerdaIcone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon iconeDireita = resizeIcon(Icones.direita, 20, 20);
        labelDireitaIcone = new JButton(iconeDireita);
        labelDireitaIcone.setOpaque(true);
        labelDireitaIcone.setBorderPainted(false);
        labelDireitaIcone.setBackground(Cor.CINZA_CLARO);
        labelDireitaIcone.setFocusPainted(false);
        labelDireitaIcone.setPreferredSize(new Dimension(30, 30));
        labelDireitaIcone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        janelaAdicionar.setLayout(new BorderLayout());
        janelaAdicionar.setMinimumSize(new Dimension(580, 400));
        janelaAdicionar.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        janelaAdicionar.setLocationRelativeTo(null);
        janelaAdicionar.setVisible(true);

        JPanel background = new JPanel();
        background.setBackground(Color.WHITE);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        JPanel blocoBranco = blocoBranco(new JPanel(), response);
        JPanel linhaCinza = linhaCinza(new JPanel());
        JPanel linhaCinza2 = linhaCinza(new JPanel());

        background.add(blocoBranco);
        background.add(linhaCinza);

        AtomicInteger i = new AtomicInteger(0);
        AtomicReference<DiariaResponse> diaria = new AtomicReference<>(response.diarias().get(i.get()));

        int diariaAtual =  Period.between(response.data_entrada(), LocalDate.now()).getDays();


        if (response.data_saida().isBefore(LocalDate.now())) {
            diariaAtual = Period.between(response.data_entrada(), response.data_saida()).getDays() - 1;
        }
        if (response.data_saida().equals(LocalDate.now())) {
            diariaAtual =  Period.between(response.data_entrada(), LocalDate.now()).getDays() - 1;
        }

        diaria.set(response.diarias().get(diariaAtual));
        i.set(diariaAtual);
        if (diaria.get().data_entrada().equals(response.data_entrada())) labelEsquerdaIcone.setEnabled(false);
        if (diaria.get().data_saida().equals(response.data_saida())) labelDireitaIcone.setEnabled(false);


        int finalDiariaAtual = diariaAtual;
        labelDireitaIcone.addActionListener(e -> {
            labelEsquerdaIcone.setEnabled(true);

            if (diaria.get().data_saida().isEqual(response.data_saida())){
                labelDireitaIcone.setEnabled(false);
            } else {
                i.getAndIncrement();
                diaria.set(response.diarias().get(i.get()));

                if (diaria.get().data_saida().isEqual(response.data_saida())){
                    i.getAndDecrement();
                    labelDireitaIcone.setEnabled(false);
                }

                atualizarPanels(background, response, diaria.get(), finalDiariaAtual);
            }
        });

        int finalDiariaAtual1 = diariaAtual;

        labelEsquerdaIcone.addActionListener(e -> {
            if (diaria.get().data_entrada().equals(response.data_entrada())) {
                labelEsquerdaIcone.setEnabled(false);
            } else {
                labelDireitaIcone.setEnabled(true);
                diaria.set(response.diarias().get(i.get()));
                i.getAndDecrement();
                if (diaria.get().numero() == 1) {
                    i.getAndIncrement();
                    labelEsquerdaIcone.setEnabled(false);
                    labelDireitaIcone.setEnabled(true);
                }
                atualizarPanels(background, response, diaria.get(), finalDiariaAtual1);
            }
        });

        JPanel blocoVisualizaDiarias = blocoVisualizarDiarias(new JPanel(), response, diaria.get(), diariaAtual);
        JPanel espacoBranco = espacoBranco(new JPanel());
        JPanel blocoPessoas = blocoPessoas(new JPanel());
        JPanel blocoListaDePessoas = blocoListaDePessoas(new JPanel(), diaria.get());
        JPanel blocoPagamentos = blocoPagamentos(new JPanel());
        JPanel blocoListaDePagamentos = blocoListaDePagamentos(new JPanel(), diaria.get());
        JPanel blocoConsumo = blocoConsumo(new JPanel(), diaria.get().consumo());
        JPanel blocoListaConsumo = blocoListaDeConsumos(new JPanel(), diaria.get().consumo());
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
        janelaAdicionar.pack();
        janelaAdicionar.revalidate();
        janelaAdicionar.repaint();
    }

    private void atualizarPanels(JPanel background, PernoiteResponse response, DiariaResponse diaria, int diariaAtual) {

        JPanel blocoBranco = blocoBranco(new JPanel(), response);
        JPanel linhaCinza = linhaCinza(new JPanel());
        JPanel linhaCinza2 = linhaCinza(new JPanel());
        JPanel blocoVisualizaDiarias = blocoVisualizarDiarias(new JPanel(), response, diaria, diariaAtual);
        JPanel espacoBranco = espacoBranco(new JPanel());
        JPanel blocoPessoas = blocoPessoas(new JPanel());
        JPanel blocoListaDePessoas = blocoListaDePessoas(new JPanel(), diaria);
        JPanel blocoPagamentos = blocoPagamentos(new JPanel());
        JPanel blocoListaDePagamentos = blocoListaDePagamentos(new JPanel(), diaria);
        JPanel blocoConsumo = blocoConsumo(new JPanel(), diaria.consumo());
        JPanel blocoListaConsumo = blocoListaDeConsumos(new JPanel(), diaria.consumo());
        JPanel blocoOpcoes = blocoBotoesOpcoes(new JPanel());

        background.removeAll();
        background.add(blocoBranco);
        background.add(linhaCinza);
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
        background.revalidate();
        background.repaint();

        janelaAdicionar.pack();
    }




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

        BotaoArredondado botaoQuarto = new BotaoArredondado(pernoite.quarto() < 10L ? "0" + pernoite.quarto() : pernoite.quarto().toString());
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

        return blocoBranco;
    }


    public JPanel blocoVisualizarDiarias(JPanel blocoVisualizaDiarias, PernoiteResponse pernoite, DiariaResponse diaria, int diariaAtual){
        blocoVisualizaDiarias.setPreferredSize(new Dimension(500, 50));
        blocoVisualizaDiarias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        blocoVisualizaDiarias.setBorder(BorderFactory.createEmptyBorder(2,10,0,0));
        blocoVisualizaDiarias.setLayout(new FlowLayout(FlowLayout.LEFT));

        ImageIcon iconeDiaria = resizeIcon(Icones.diarias_quantidade, 25, 25);

        JLabel labelDiariaIcone = new JLabel(iconeDiaria);
        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

        JLabel numeroDiaria = new JLabel(diaria.numero().toString());
        numeroDiaria.setFont(new Font("Inter", Font.BOLD, 20));
        numeroDiaria.setForeground(Cor.CINZA_ESCURO);


        labelDiariaIcone.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        LabelArredondado labelDataEntrada = new LabelArredondado(diaria.data_entrada().format(DateTimeFormatter.ofPattern("dd/MM")));
        labelDataEntrada.setToolTipText("Data de entrada");
        labelDataEntrada.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataEntrada.setForeground(Cor.CINZA_ESCURO);
        labelDataEntrada.setBackground(Color.white);
        labelDataEntrada.setOpaque(false);
        labelDataEntrada.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        LabelArredondado labelDataSaida = new LabelArredondado(diaria.data_saida().format(DateTimeFormatter.ofPattern("dd/MM")));
        labelDataSaida.setToolTipText("Data de saida");
        labelDataSaida.setFont(new Font("Inter", Font.BOLD, 20));
        labelDataSaida.setForeground(Cor.CINZA_ESCURO);
        labelDataSaida.setBackground(Color.white);
        labelDataSaida.setOpaque(false);
        labelDataSaida.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        LabelArredondado isDiariaAtualLabel = new LabelArredondado("Diária atual");
        isDiariaAtualLabel.setBackground(Cor.VERDE_ESCURO);
        isDiariaAtualLabel.setForeground(Cor.CINZA_CLARO.brighter());

        blocoVisualizaDiarias.add(labelDiariaIcone);
        blocoVisualizaDiarias.add(labelEsquerdaIcone);
        blocoVisualizaDiarias.add(numeroDiaria);
        blocoVisualizaDiarias.add(labelDireitaIcone);
        blocoVisualizaDiarias.add(labelDataEntrada);
        blocoVisualizaDiarias.add(labelDataSaida);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime entrada = diaria.data_entrada().atTime(12, 0);
        LocalDateTime saida = diaria.data_saida().atTime(12, 0);

        if (now.isAfter(entrada) && now.isBefore(saida)) {
            blocoVisualizaDiarias.add(isDiariaAtualLabel);
        }

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
        blocoOpcoes.setBackground(Color.WHITE);
        blocoOpcoes.setLayout(new BorderLayout());
        blocoOpcoes.setBorder(BorderFactory.createEmptyBorder(5,5,5,10));

        JButton cancelar = new JButton("Cancelar");
        cancelar.setFocusPainted(false);
        cancelar.setPreferredSize(new Dimension(100, 30));
        cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelar.setBackground(Color.RED);
        cancelar.setForeground(Cor.BRANCO.brighter());
        blocoOpcoes.add(cancelar, BorderLayout.EAST);

        JButton finalizar = new JButton("Finalizar");
        finalizar.setFocusPainted(false);
        finalizar.setPreferredSize(new Dimension(100, 30));
        finalizar.setForeground(Cor.CINZA_ESCURO);
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

        JPanel panelPessoas = new JPanel();
        panelPessoas.add(labelPessoasIcone);
        panelPessoas.setBackground(Cor.AZUL_ESCURO);
        panelPessoas.add(labelPessoas);

        blocoPessoas.add(panelPessoas, BorderLayout.WEST);

        JButton adicionarPessoas = new JButton("Adicionar");
        adicionarPessoas.setFocusPainted(false);
        adicionarPessoas.setForeground(Cor.CINZA_ESCURO);
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

            JPanel pessoaPanel = new JPanel();
            pessoaPanel.setLayout(new BorderLayout());
            pessoaPanel.setPreferredSize(new Dimension(545, 40));  // Tamanho fixo para os paineis de pessoas
            pessoaPanel.setMaximumSize(new Dimension(545, 40));
            pessoaPanel.setMinimumSize(new Dimension(545, 40));

            JPanel pessoaInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pessoaInfoPanel.setOpaque(false);
            pessoaInfoPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

            JLabel nome = new JLabel(pessoa.nome() + "  ");
            nome.setFont(new Font("Inter", Font.BOLD, 15));
            nome.setForeground(Cor.CINZA_ESCURO);

            pessoaInfoPanel.add(pessoaIcone);

            pessoaInfoPanel.add(nome);

            JLabel telefone = new JLabel(pessoa.telefone());
            telefone.setFont(new Font("Inter", Font.BOLD, 15));
            telefone.setForeground(Cor.CINZA_ESCURO);
            telefone.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

            pessoaPanel.add(pessoaInfoPanel, BorderLayout.WEST);
            pessoaPanel.add(telefone, BorderLayout.EAST);

            blocoListaPessoas.add(pessoaPanel);

            // Adiciona um espaço rígido de 10 pixels entre os painéis
            blocoListaPessoas.add(Box.createRigidArea(new Dimension(0, 3)));
        });

        blocoListaPessoas.revalidate();
        blocoListaPessoas.repaint();
        return blocoListaPessoas;
    }




    public JPanel blocoListaDePagamentos(JPanel blocoListaDePagamentos, DiariaResponse diaria){
        blocoListaDePagamentos.setLayout(new BoxLayout(blocoListaDePagamentos, BoxLayout.Y_AXIS));
        blocoListaDePagamentos.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        blocoListaDePagamentos.setBackground(Color.WHITE);

        diaria.pagamento().forEach(pagamento -> {
            JLabel pagamentoIcone = new JLabel(Converter.converterIconePagamento(pagamento.tipo_pagamento()));

            PanelArredondado pagamentoPanel = new PanelArredondado();
            pagamentoPanel.setLayout(new BorderLayout());
            pagamentoPanel.setPreferredSize(new Dimension(545, 40));
            pagamentoPanel.setMaximumSize(new Dimension(545, 40));
            pagamentoPanel.setMinimumSize(new Dimension(545, 40));

            JPanel pagamentoInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pagamentoInfoPanel.setOpaque(false);
            pagamentoInfoPanel.setBorder(BorderFactory.createEmptyBorder(1,10,0,0));

            JLabel id = new JLabel(" #" + pagamento.pagamento_id());
            id.setFont(new Font("Inter", Font.BOLD, 15));
            id.setForeground(Color.RED);

            JLabel tipoPagamento = new JLabel(pagamento.tipo_pagamento() + "  ");
            tipoPagamento.setFont(new Font("Inter", Font.BOLD, 15));
            tipoPagamento.setForeground(Cor.CINZA_ESCURO);

            pagamentoInfoPanel.add(pagamentoIcone);
            pagamentoInfoPanel.add(tipoPagamento);

            JLabel valor = new JLabel("R$ " + FormatarFloat.format(pagamento.valor_pagamento()));
            valor.setFont(new Font("Inter", Font.BOLD, 15));
            valor.setForeground(Cor.CINZA_ESCURO);
            valor.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));

            pagamentoPanel.add(pagamentoInfoPanel, BorderLayout.WEST);
            pagamentoPanel.add(valor, BorderLayout.EAST);

            blocoListaDePagamentos.add(pagamentoPanel);

            // Adiciona um espaço rígido de 10 pixels entre os painéis
            blocoListaDePagamentos.add(Box.createRigidArea(new Dimension(0, 3)));
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
        adicionarPagamento.setForeground(Cor.CINZA_ESCURO);
        adicionarPagamento.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        blocoPagamento.add(adicionarPagamento, BorderLayout.EAST);

        return blocoPagamento;
    }


    public JPanel blocoConsumo(JPanel blocoConsumo, DiariaResponse.Consumo consumo) {
        blocoConsumo.setPreferredSize(new Dimension(500, 45));
        blocoConsumo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        blocoConsumo.setBackground(Cor.AZUL_ESCURO);
        blocoConsumo.setLayout(new BorderLayout());
        blocoConsumo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon iconeConsumo = resizeIcon(Icones.sacola_branca, 20, 20);
        JLabel consumoIcone = new JLabel(iconeConsumo);

        JLabel labelConsumo = new JLabel(" Total Consumo");
        labelConsumo.setFont(new Font("Inter", Font.BOLD, 20));
        labelConsumo.setForeground(Color.WHITE);

        JPanel panelConsumo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelConsumo.add(consumoIcone);
        panelConsumo.setBackground(Cor.AZUL_ESCURO);
        panelConsumo.add(labelConsumo);

        blocoConsumo.add(panelConsumo, BorderLayout.WEST);

        JPanel valorEBotaoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        valorEBotaoPanel.setBackground(Cor.AZUL_ESCURO);

        JLabel labelTotalConsumo = new JLabel("R$ " + FormatarFloat.format(consumo.total_consumo()));
        labelTotalConsumo.setFont(new Font("Inter", Font.BOLD, 20));
        labelTotalConsumo.setForeground(Color.WHITE);

        JButton adicionarConsumo = new JButton("Adicionar");
        adicionarConsumo.setFocusPainted(false);
        adicionarConsumo.setForeground(Cor.CINZA_ESCURO);
        adicionarConsumo.setPreferredSize(new Dimension(100, 30));
        adicionarConsumo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        valorEBotaoPanel.add(labelTotalConsumo);
        valorEBotaoPanel.add(adicionarConsumo);

        blocoConsumo.add(valorEBotaoPanel, BorderLayout.EAST);

        return blocoConsumo;
    }


    public JPanel blocoListaDeConsumos(JPanel blocoListaDeConsumos, DiariaResponse.Consumo consumos){
        blocoListaDeConsumos.setLayout(new BoxLayout(blocoListaDeConsumos, BoxLayout.Y_AXIS));
        blocoListaDeConsumos.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        blocoListaDeConsumos.setBackground(Color.WHITE);

        consumos.itens().forEach(item -> {
            JLabel itemIcone = new JLabel(resizeIcon(Icones.diarias_quantidade, 25,25));

            PanelArredondado itemPanel = new PanelArredondado();
            itemPanel.setLayout(new BorderLayout());
            itemPanel.setPreferredSize(new Dimension(545, 40));
            itemPanel.setMaximumSize(new Dimension(545, 40));
            itemPanel.setMinimumSize(new Dimension(545, 40));

            JPanel itemInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            itemInfoPanel.setOpaque(false);
            itemInfoPanel.setBorder(BorderFactory.createEmptyBorder(1,10,0,0));

            JLabel itemLabel = new JLabel(item.item());
            itemLabel.setFont(new Font("Inter", Font.BOLD, 15));
            itemLabel.setForeground(Cor.CINZA_ESCURO);

            itemInfoPanel.add(itemIcone);
            itemInfoPanel.add(itemLabel);

            JPanel valorQuantidadePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            valorQuantidadePanel.setOpaque(false);
            valorQuantidadePanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,10));

            JLabel valor = new JLabel("R$ " + FormatarFloat.format(item.valor_item()));
            valor.setFont(new Font("Inter", Font.BOLD, 15));
            valor.setForeground(Cor.CINZA_ESCURO);

            JLabel quantidade = new JLabel("x" + item.quantidade());
            quantidade.setFont(new Font("Inter", Font.BOLD, 15));
            quantidade.setForeground(Cor.CINZA_ESCURO);

            float subtotalsom = item.quantidade() * item.valor_item();

            JLabel subtotal = new JLabel(FormatarFloat.format(subtotalsom));
            subtotal.setFont(new Font("Inter", Font.BOLD, 15));
            subtotal.setForeground(Cor.CINZA_ESCURO);

            valorQuantidadePanel.add(quantidade);
            valorQuantidadePanel.add(valor);
            valorQuantidadePanel.add(subtotal);

            itemPanel.add(itemInfoPanel, BorderLayout.WEST);
            itemPanel.add(valorQuantidadePanel, BorderLayout.EAST);

            blocoListaDeConsumos.add(itemPanel);
            blocoListaDeConsumos.add(Box.createRigidArea(new Dimension(0, 3)));
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
}