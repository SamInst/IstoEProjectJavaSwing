package menu.panels.quartosPanel;

import response.QuartoResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TabelaPrecoPorQuartoFrame extends JFrame {
    public TabelaPrecoPorQuartoFrame(List<QuartoResponse.Categoria.ValorPessoa> valorPessoaList) {
        setTitle("Tabela de Preços por Quarto");
        setSize(400, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        String[] columnNames = {"Quantidade de Pessoas", "Valor"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (QuartoResponse.Categoria.ValorPessoa valorPessoa : valorPessoaList) {
            Object[] rowData = {
                    valorPessoa.qtd_pessoa(),
                    valorPessoa.valor()
            };
            tableModel.addRow(rowData);
        }

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
