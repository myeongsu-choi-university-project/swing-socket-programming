package userInterface.panel;

import dto.EmailInfo;
import service.IMAPEmailInfoFetcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class MailBoxDisplay extends JPanel {
    private EmailInfoDisplay emailInfoDisplay;
    private IMAPEmailInfoFetcher emailInfoFetcher;
    private String currentFolder;
    private int currentPage = 0;
    private int emailsPerPage = 10;

    public MailBoxDisplay() {
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 버튼 생성 및 스타일 적용
        JButton inboxButton = createStyledButton("받은 메일함");
        inboxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("INBOX");
            }
        });

        JButton sentButton = createStyledButton("보낸 메일함");
        sentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("\"Sent Messages\"");
            }
        });

        JButton trashButton = createStyledButton("휴지통");
        trashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage = 0;
                loadEmails("Trash");
            }
        });

        // 버튼을 패널에 추가
        add(inboxButton);
        add(Box.createVerticalStrut(10)); // 버튼 사이에 간격 추가
        add(sentButton);
        add(Box.createVerticalStrut(10)); // 버튼 사이에 간격 추가
        add(trashButton);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼을 중앙에 정렬
        button.setBackground(new Color(70, 130, 180)); // 버튼 배경색 설정 (스틸 블루)
        button.setForeground(Color.WHITE); // 버튼 텍스트 색상 설정
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // 버튼 폰트 설정
        button.setFocusPainted(false); // 포커스 표시 제거
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 버튼 패딩 설정

        // 버튼이 부모 컨테이너의 너비에 맞게 늘어나도록 설정
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

        // 마우스 호버 시
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        return button;
    }

    private void loadEmails(String folderName) {
        try {
            currentFolder = folderName;
            emailInfoFetcher = new IMAPEmailInfoFetcher(folderName);
            List<EmailInfo> emailInfoList = emailInfoFetcher.fetchEmailInfo(folderName, currentPage, emailsPerPage);
            emailInfoDisplay.updateEmailList(emailInfoList);;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load emails from " + folderName,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setEmailInfoDisplay(EmailInfoDisplay emailInfoDisplay) {
        this.emailInfoDisplay = emailInfoDisplay;
        loadEmails("INBOX");
    }

    public void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadEmails(currentFolder);
        }
    }

    public void nextPage() {
        currentPage++;
        loadEmails(currentFolder);
    }
}
