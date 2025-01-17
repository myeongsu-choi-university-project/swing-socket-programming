package userInterface.frame;

import service.IMAPLogin;
import userInterface.panel.UserLoginPage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    public UserLoginPage userLoginPage;
    public Main() {
        setTitle("Email Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 노트북 너비와 높이에 맞게 창크기 조절
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.9);
        setSize(width, height);
        setLocationRelativeTo(null);

        userLoginPage = new UserLoginPage(this);
        add(userLoginPage);
        userLoginPage.setVisible(true);
        setVisible(true);
    }

    // 로그인 시도 메서드
    public boolean authenticate(String email, String password) throws IOException {
        boolean loginSuccess = false;

        // Naver
        if (email.endsWith("@naver.com")) {
            IMAPLogin naverLogin = new IMAPLogin(email, password);
            loginSuccess = naverLogin.login();
        }
        else {
            JOptionPane.showMessageDialog(userLoginPage, "지원되지 않는 이메일입니다.");
            return false;
        }

        if (loginSuccess) {
            JOptionPane.showMessageDialog(userLoginPage, "로그인합니다.");
            return true;
        } else {
            JOptionPane.showMessageDialog(userLoginPage, "Email이나 Password 정보가 맞지 않습니다.");
            return false;
        }
    }

    // 로그인 성공 후 Frame 변경
    public void showUserFrame(String email, String password) {
        AppFrame appFrame = new AppFrame(email, password);
        userLoginPage.setVisible(false);
        this.dispose();
    }

    public static void main(String[] args) {
        new Main();
    }
}