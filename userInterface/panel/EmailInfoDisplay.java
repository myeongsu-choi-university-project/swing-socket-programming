package userInterface.panel;

import dto.EmailInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmailInfoDisplay extends JPanel {
    private MailBoxDisplay mailBoxDisplay;  // 메일함 디스플레이 참조

    public EmailInfoDisplay(MailBoxDisplay mailBoxDisplay) {
        this.mailBoxDisplay = mailBoxDisplay;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 이메일 목록을 표시할 패널 생성
        JPanel emailListPanel = new JPanel();
        emailListPanel.setBackground(new Color(245, 245, 245));
        emailListPanel.setLayout(new BoxLayout(emailListPanel, BoxLayout.Y_AXIS));

        // JScrollPane 생성 및 emailListPanel 추가
        JScrollPane scrollPane = new JScrollPane(emailListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 이전 및 다음 버튼 생성
        JButton prevButton = createStyledButton("이전");
        JButton nextButton = createStyledButton("다음");

        prevButton.addActionListener(e -> {
            mailBoxDisplay.prevPage();
            scrollPane.getVerticalScrollBar().setValue(0);
        });
        nextButton.addActionListener(e -> {
            mailBoxDisplay.nextPage();
            scrollPane.getVerticalScrollBar().setValue(0);
        });

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼을 중앙에 정렬
        button.setBackground(new Color(70, 130, 180)); // 버튼 배경색 설정 (스틸 블루)
        button.setForeground(Color.WHITE); // 버튼 텍스트 색상 설정
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // 버튼 폰트 설정
        button.setFocusPainted(false); // 포커스 표시 제거
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 버튼 패딩 설정

        // 마우스 호버 시
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        return button;
    }

    public void updateEmailList(List<EmailInfo> emailInfoList) {

    }
}
