package userInterface.panel;

import service.ConnectionManager;
import userInterface.frame.Main;
import userInterface.frame.AppFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class UserInfoDisplay extends JPanel {
    private AppFrame appFrame;    // 부모 프레임 참조 (로그아웃 후 전환 시 필요)
    private String email;         // 현재 로그인된 사용자의 이메일 주소

    // 이메일 정보를 표시하고 로그아웃 버튼을 초기화
    public UserInfoDisplay(AppFrame appFrame, String email) {
        this.appFrame = appFrame;
        this.email = email;

        initUI();
    }

    // 패널의 UI 요소를 초기화하고 레이아웃 설정하는 메서드
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.white);

        // 이메일을 표시할 JLabel 생성
        JLabel emailLabel = new JLabel(" \uD83D\uDCE7 " + email);
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(Color.DARK_GRAY);

        // 로그아웃 버튼 생성
        JButton logoutButton = new JButton("로그 아웃");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutButton.setBackground(new Color(219, 68, 55));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // ConnectionManager를 통해 로그아웃 수행
                    ConnectionManager.getInstance().close();
                    JOptionPane.showMessageDialog(appFrame, "로그아웃합니다.");

                    // 로그아웃 후 로그인 화면으로 전환
                    Main main = new Main();
                    appFrame.dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(appFrame, "로그아웃 에러: " + ex.getMessage(),
                            "로그아웃 에러", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // 테두리 제거하고 안쪽 여백만 추가
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 중앙에 이메일 라벨 추가
        add(emailLabel, BorderLayout.NORTH);
        add(Box.createRigidArea(new Dimension(0, 15))); // 15픽셀 간격 추가
        add(logoutButton, BorderLayout.SOUTH);
    }
}
