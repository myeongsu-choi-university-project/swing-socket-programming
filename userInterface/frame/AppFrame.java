package userInterface.frame;

import userInterface.panel.UserInfoDisplay;
import userInterface.panel.EmailInfoDisplay;
import userInterface.panel.MailBoxDisplay;
import userInterface.panel.SenderGUI;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private String email;                     // 사용자의 이메일 주소
    private String password;
    private UserInfoDisplay userInfoDisplay;  // 이메일 정보를 표시하는 패널 (왼쪽 상단)
    private MailBoxDisplay mailBoxDisplay;    // 메일함 관리 패널 (왼쪽 하단)
    private EmailInfoDisplay emailInfo;       // 선택한 이메일 상세 정보 패널 (오른쪽 상단)
    private SenderGUI senderGUI;              // 이메일 전송 인터페이스 (오른쪽 하단)

    public AppFrame(String email, String password) {
        this.email = email;
        this.password = password;

        setTitle("Email Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.white);

        initUI();

        setVisible(true);
    }

    public void initUI() {
        // 노트북 너비와 높이에 맞게 창크기 조절
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.9);
        int height = (int) (screenSize.height * 0.9);
        setSize(width, height);
        setLocationRelativeTo(null);

        // GridBagLayout으로 레이아웃 설정
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // EmailDisplay 설정 - 왼쪽 상단 일부 영역
        userInfoDisplay = new UserInfoDisplay(this, email);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 10, 5, 5);
        add(userInfoDisplay, gbc);

        // MailBoxDisplay 설정 - EmailDisplay 아래 위치
        mailBoxDisplay = new MailBoxDisplay();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 10, 5);
        add(mailBoxDisplay, gbc);

        // EmailInfoDisplay 패널 설정 - 오른쪽 상단
        emailInfo = new EmailInfoDisplay(mailBoxDisplay);
        mailBoxDisplay.setEmailInfoDisplay(emailInfo);  // MailBoxDisplay와 EmailInfoDisplay 연결
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 0.4;
        gbc.insets = new Insets(10, 5, 5, 10);
        add(emailInfo, gbc);

        // SenderGUI 패널 설정 - EmailInfo 아래
        senderGUI = new SenderGUI(email, password);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 0.6;
        gbc.insets = new Insets(5, 5, 10, 10);
        add(senderGUI, gbc);
    }
}
