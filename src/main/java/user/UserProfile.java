package user;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class UserProfile {
    private static String username;
    private static String email;
    private static String path_profile;

//    public UserProfile(String username, String email, String path_profile) {
//        this.username = username;
//        this.email = email;
//        this.path_profile = path_profile;
//    }
//
//    public UserProfile() {}

    public void setUserConfiguration(String nome, String email, String profile) {
        this.username = nome;
        this.email = email;
        this.path_profile = profile;
        System.out.println(nome);
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getPath_profile() {
        return path_profile;
    }
}
