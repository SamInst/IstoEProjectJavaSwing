package repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static config.PostgresDatabaseConnect.connect;

public class UsuarioRepository {

    private static final Logger logger = Logger.getLogger(UsuarioRepository.class.getName());
    Connection conexao = connect();

    public void criarUsuario(String username, String senha) {
        String senhaEncriptada = encriptarMD5(senha);
        String sql = "INSERT INTO usuarios (username, senha, bloqueado) VALUES (?, ?, ?);";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, senhaEncriptada);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao criar usuário", e);
        }
    }

    public boolean verificarUsuarioESenha(String username, String senha) {
        String senhaEncriptada = encriptarMD5(senha);
        String sql = "SELECT * FROM usuarios WHERE username = ? AND senha = ?";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, senhaEncriptada);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar usuário e senha", e);
        }
        return false;
    }

    private String encriptarMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Erro ao encriptar senha em MD5", e);
            throw new RuntimeException(e);
        }
    }
}



