package login.component;

import buttons.ShadowButton;
import login.swing.MyPasswordField;
import login.swing.MyTextField;
import lombok.SneakyThrows;
import menu.Menu;
import net.miginfocom.swing.MigLayout;
import repository.UsuarioRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static config.PostgresDatabaseConnect.connect;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static notifications.Notification.notification;
import static notifications.Notifications.Location.*;
import static notifications.Notifications.Type;
import static notifications.Notifications.Type.SUCCESS;
import static notifications.Notifications.Type.WARNING;
import static tools.Icones.login_pass;
import static tools.Icones.login_user;

public class PanelLoginAndRegister extends JLayeredPane {
    UsuarioRepository usuarioRepository = new UsuarioRepository();

    public PanelLoginAndRegister(JFrame frame) {
        initComponents();
        initRegister(frame);
        initLogin(frame);
        login.setVisible(false);
        register.setVisible(true);
    }

    private void initRegister(JFrame frame) {
        register.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel label = new JLabel("Criar Perfil");
        label.setFont(new Font("sansserif", Font.BOLD, 30));
        label.setForeground(new Color(7, 164, 121));
        register.add(label);

        MyTextField txtUser = new MyTextField();
        txtUser.setPrefixIcon(login_user);
        txtUser.setHint("Usuário");
        register.add(txtUser, "w 60%");

        MyPasswordField txtPass = new MyPasswordField();
        txtPass.setPrefixIcon(login_pass);
        txtPass.setHint("Senha");
        register.add(txtPass, "w 60%");

        ShadowButton cmd = new ShadowButton();
        cmd.setBackground(new Color(7, 164, 121));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setFocusPainted(false);
        cmd.setText("CADASTRAR");
        register.add(cmd, "w 40%, h 40");

        registerNewEmployee(cmd, txtUser, txtPass, frame);
    }

    private void registerNewEmployee(ShadowButton cmd, MyTextField txtUser, MyPasswordField txtPass, JFrame frame){
        cmd.addActionListener(e -> {
            String username = txtUser.getText();
            String password = txtPass.getText();

            if (username.isEmpty() || password.isEmpty()) {
                notification(WARNING, TOP_RIGHT, "Campos de usuário e senha não podem ficar vazios!");
                return;
            }

            if (usuarioRepository.verificarUsuarioESenha(username, password)) {
                notification(WARNING, TOP_RIGHT, "Usuário já cadastrado");
                return;
            }

            try {
                usuarioRepository.criarUsuario(username, password);
                notification(SUCCESS, TOP_RIGHT, "Usuário " + username.toUpperCase() + " cadastrado! Faça login");
            } catch (Exception ex){
                notification(Type.ERROR, TOP_RIGHT, "Erro ao cadastrar perfil: " + ex);
            }
        });
    }

    private void initLogin(JFrame frame) {
        login.setLayout(new MigLayout("wrap", "push[center]push", "push[]25[]10[]10[]25[]push"));
        JLabel label = new JLabel("Entrar");
        label.setFont(new Font("sansserif", Font.BOLD, 30));
        label.setForeground(new Color(7, 164, 121));
        login.add(label);
        MyTextField txtEmail = new MyTextField();
        txtEmail.setPrefixIcon(login_user);
        txtEmail.setHint("Usuário");
        login.add(txtEmail, "w 60%");
        MyPasswordField txtPass = new MyPasswordField();
        txtPass.setPrefixIcon(login_pass);
        txtPass.setHint("Senha");
        login.add(txtPass, "w 60%");

        ShadowButton cmd = new ShadowButton();
        cmd.setBackground(new Color(7, 16, 12));
        cmd.setForeground(new Color(250, 250, 250));
        cmd.setFocusPainted(false);
        cmd.setText("ENTRAR");
        login.add(cmd, "w 40%, h 40");
        loginEmployee(cmd, txtEmail, txtPass, frame);
    }

    private void loginEmployee(ShadowButton cmd, MyTextField txtUser, MyPasswordField txtPass, JFrame frame){
        cmd.addActionListener(new ActionListener() {
            @SneakyThrows
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connect() == null)
                    notification(Type.ERROR, TOP_LEFT, "Erro de conexão com o Banco de Dados\nVerifique as credenciais cadastradas");

                String username = txtUser.getText();
                String password = txtPass.getText();

                if (username.isEmpty() || password.isEmpty()) {
                    notification( WARNING, TOP_LEFT, "Campos de usuário e senha não podem ficar vazios!");
                    return;
                }

                if (!usuarioRepository.verificarUsuarioESenha(username, password)) {
                    notification( Type.ERROR, TOP_LEFT, "Usuário não cadastrado");
                } else {
                    frame.dispose();
                    notification(SUCCESS, TOP_CENTER, "Login Efetuado com sucesso!\nUsuário: " + username.toUpperCase());
                }
            }
        });
    }

    public void showRegister(boolean show) {
        if (show) {
            register.setVisible(true);
            login.setVisible(false);
        } else {
            register.setVisible(false);
            login.setVisible(true);
        }
    }

    private void initComponents() {
        login = new JPanel();
        register = new JPanel();

        setLayout(new CardLayout());

        login.setBackground(new Color(255, 255, 255));

        GroupLayout loginLayout = new GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        register.setBackground(new Color(255, 255, 255));

        GroupLayout registerLayout = new GroupLayout(register);
        register.setLayout(registerLayout);
        registerLayout.setHorizontalGroup(
            registerLayout.createParallelGroup(LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        registerLayout.setVerticalGroup(
            registerLayout.createParallelGroup(LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(register, "card2");
    }

    private JPanel login;
    private JPanel register;
}
