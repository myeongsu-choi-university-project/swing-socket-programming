package userInterface.panel;

import userInterface.frame.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UserLoginPage extends JPanel {
    private JTextField emailField;          // 이메일 입력 필드
    private JPasswordField passwordField;   // 비밀번호 입력 필드
    private JButton loginButton;            // 로그인 버튼

    public UserLoginPage(Main emailFrame) {
        setBackground(Color.white);
        setLayout(new GridBagLayout());

        initUI(emailFrame);
    }

    private void initUI(Main emailFrame) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 제목 라벨
        JLabel titleLabel = new JLabel("로그인");
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 20;
        add(titleLabel, gbc);

        // 이메일 입력
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipady = 10;
        add(emailLabel, gbc);

        emailField = new JTextField(30); // 필드로 선언
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipady = 15;
        add(emailField, gbc);

        // 비밀번호 입력
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(30); // 필드로 선언
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // 로그인 버튼
        loginButton = new JButton("Login"); // 필드로 선언
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipady = 20;
        add(loginButton, gbc);

        // 이벤트 핸들러 등록
        registerEvents(emailFrame);
    }

    private void registerEvents(Main emailFrame) {
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword()); // JPasswordField는 보안상 char[] 배열로 반환하기 때문에 문자열로 변환

            try {
                boolean loginSuccess = emailFrame.authenticate(email, password);

                if(loginSuccess)
                    emailFrame.showUserFrame(email, password);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "로그인 중 오류가 발생했습니다.");
            }
        });

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER)
                    loginButton.doClick();
            }
        };

        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
}
