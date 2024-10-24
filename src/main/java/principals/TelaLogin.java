package principals;

import principals.tools.Icones;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

record LoginInfo(String user, String senha) {}

public class TelaLogin extends JFrame {

    private final String nomePousada;
    private final String subtituloPousada;
    private JTextField campoUsuario;
    private JPasswordField campoSenha;

    public TelaLogin(String nomePousada, String subtituloPousada) {
        this.nomePousada = nomePousada;
        this.subtituloPousada = subtituloPousada;
        setIconImage(Icones.logo.getImage());
        setTitle("Login");
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelFundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon fundoIcon = new ImageIcon("src/main/resources/paisagens/paisagem1.jpg");
                Image fundo = fundoIcon.getImage();
                g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        painelFundo.setLayout(null);

        JLabel labelNomePousada = new JLabel(nomePousada);
        labelNomePousada.setFont(new Font("Inter", Font.BOLD, 48));
        labelNomePousada.setForeground(Color.WHITE);
        labelNomePousada.setBounds(100, 50, 600, 60);
        painelFundo.add(labelNomePousada);

        JLabel labelSubtituloPousada = new JLabel(subtituloPousada);
        labelSubtituloPousada.setFont(new Font("Arial", Font.PLAIN, 24));
        labelSubtituloPousada.setForeground(Color.WHITE);
        labelSubtituloPousada.setBounds(100, 120, 600, 40);
        painelFundo.add(labelSubtituloPousada);

        JPanel painelLogin = new JPanel();
        painelLogin.setLayout(null);
        painelLogin.setBackground(new Color(128, 128, 192, 200));
        painelLogin.setSize(350, 250);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - painelLogin.getWidth()) / 2;
        int y = (screenSize.height - painelLogin.getHeight()) / 2;
        painelLogin.setBounds(x, y, painelLogin.getWidth(), painelLogin.getHeight());

        painelFundo.add(painelLogin);

        JLabel labelLogin = new JLabel("LOGIN");
        labelLogin.setFont(new Font("Arial", Font.BOLD, 24));
        labelLogin.setBounds(130, 20, 100, 30);
        painelLogin.add(labelLogin);

        JLabel labelUsuario = new JLabel("USUÁRIO:");
        labelUsuario.setBounds(30, 70, 100, 20);
        painelLogin.add(labelUsuario);

        campoUsuario = new JTextField();
        campoUsuario.setBounds(30, 100, 290, 30);
        painelLogin.add(campoUsuario);

        JLabel labelSenha = new JLabel("SENHA:");
        labelSenha.setBounds(30, 140, 100, 20);
        painelLogin.add(labelSenha);

        campoSenha = new JPasswordField();
        campoSenha.setBounds(30, 170, 290, 30);
        painelLogin.add(campoSenha);

        JButton botaoEntrar = new JButton("login");
        botaoEntrar.setBounds(130, 210, 100, 30);
        painelLogin.add(botaoEntrar);

        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = campoUsuario.getText();
                String senha = new String(campoSenha.getPassword());

                Menu.main(new String[0]);
                dispose();
            }
        });

        add(painelFundo);
    }

    public static void main(String[] args) {
        String nomePousada = "Isto É Pousada";
        String subtituloPousada = "Referência na baixada maranhense!";

        TelaLogin telaLogin = new TelaLogin(nomePousada, subtituloPousada);
        telaLogin.setVisible(true);
    }
}
