package userInterface.panel;

import service.SMTPSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * SenderGUI 클래스: 이메일 작성 및 전송을 위한 GUI 패널
 * JPanel을 상속받아 UI 구성 및 이메일 전송 이벤트를 처리
 */
public class SenderGUI extends JPanel {
    private String sender;              // 발신자 이메일 주소
    private String password;            // 발신자 이메일 비밀번호
    private JTextField recipientField;  // 수신자 이메일 입력 필드
    private JTextField subjectField;    // 이메일 제목 입력 필드
    private JTextField attachmentField; // 첨부 파일 경로 입력 필드
    private JTextArea bodyArea;         // 이메일 본문 입력 영역
    private JLabel statusLabel;         // 전송 상태를 표시하는 라벨

    /**
     * 생성자: 발신자 이메일 정보 초기화 및 UI 구성
     *
     * @param email    발신자 이메일 주소
     * @param password 발신자 이메일 비밀번호
     */
    public SenderGUI(String email, String password) {
        this.sender = email;
        this.password = password;

        initUI(); // UI 구성 메서드 호출
    }

    /**
     * 패널의 UI 구성
     */
    private void initUI() {
        setLayout(new BorderLayout(15, 15)); // 레이아웃 설정
        setBackground(Color.WHITE);                     // 배경 색 설정

        // 테두리 추가 (예: 라인 테두리)
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),               // 바깥쪽 라인 테두리
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // 안쪽 여백
        ));

        // 공통 스타일 설정
        Font labelFont = new Font("SansSerif", Font.BOLD, 12);  // 라벨용 폰트
        Font inputFont = new Font("SansSerif", Font.PLAIN, 14); // 입력 필드용 폰트

        // 상단: 입력 필드 패널 구성
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // 4행 2열 그리드
        inputPanel.setOpaque(false); // 배경 투명

        // 발신자 이메일 정보 표시
        JLabel senderLabel = new JLabel("발신자 이메일:");
        senderLabel.setFont(labelFont);
        JLabel senderValue = new JLabel(sender); // 발신자 이메일 값
        senderValue.setFont(inputFont);
        senderValue.setForeground(new Color(60, 60, 60)); // 텍스트 색상

        inputPanel.add(senderLabel);
        inputPanel.add(senderValue);

        // 수신자 이메일 입력
        JLabel recipientLabel = new JLabel("수신자 이메일:");
        recipientLabel.setFont(labelFont);
        recipientField = new JTextField(); // 입력 필드 생성
        recipientField.setFont(inputFont);

        inputPanel.add(recipientLabel);
        inputPanel.add(recipientField);

        // 이메일 제목 입력
        JLabel subjectLabel = new JLabel("이메일 제목:");
        subjectLabel.setFont(labelFont);
        subjectField = new JTextField();
        subjectField.setFont(inputFont);

        inputPanel.add(subjectLabel);
        inputPanel.add(subjectField);

        // 첨부 파일 경로 입력
        JLabel attachmentLabel = new JLabel("첨부 파일 경로:");
        attachmentLabel.setFont(labelFont);
        attachmentField = new JTextField();
        attachmentField.setFont(inputFont);

        inputPanel.add(attachmentLabel);
        inputPanel.add(attachmentField);

        add(inputPanel, BorderLayout.NORTH); // 상단에 입력 패널 추가

        // 중앙: 이메일 본문 입력 영역
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setOpaque(false);
        JLabel bodyLabel = new JLabel("이메일 본문");
        bodyLabel.setFont(labelFont);

        bodyArea = new JTextArea(8, 30); // 텍스트 영역
        bodyArea.setFont(inputFont);
        bodyArea.setLineWrap(true); // 줄바꿈 설정
        bodyArea.setWrapStyleWord(true); // 단어 기준 줄바꿈
        JScrollPane scrollPane = new JScrollPane(bodyArea); // 스크롤 지원
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        bodyPanel.add(bodyLabel, BorderLayout.NORTH);
        bodyPanel.add(scrollPane, BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER); // 중앙에 본문 입력 패널 추가

        // 하단: 전송 버튼 및 상태 표시 라벨
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        actionPanel.setOpaque(false);

        JButton sendButton = new JButton("이메일 전송");
        sendButton.setBackground(new Color(52, 152, 219)); // 버튼 색상
        sendButton.setForeground(Color.WHITE); // 텍스트 색상
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14)); // 버튼 폰트
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        statusLabel = new JLabel("상태: 대기 중", SwingConstants.LEFT);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(Color.RED); // 초기 상태 색상

        actionPanel.add(sendButton, BorderLayout.EAST); // 버튼을 오른쪽에 배치
        actionPanel.add(statusLabel, BorderLayout.CENTER); // 상태 표시를 중앙에 배치

        add(actionPanel, BorderLayout.SOUTH); // 하단에 액션 패널 추가

        // 전송 버튼 클릭 이벤트 처리
        sendButton.addActionListener(e -> sendEmail());
    }

    /**
     * 이메일 전송 처리
     */
    private void sendEmail() {
        String recipient = recipientField.getText();
        String subject = subjectField.getText();
        String attachment = attachmentField.getText();
        String body = bodyArea.getText();

        // 입력 검증: 비어 있는 필드가 있는지 확인
        if (recipient.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            statusLabel.setText("상태: 모든 필드를 입력하세요!");
            return; // 입력이 누락되었을 경우 종료
        }

        // 상태 업데이트: 전송 중 표시
        statusLabel.setText("상태: 이메일 전송 중...");

        // 별도의 스레드에서 전송 처리
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = SMTPSender.sendEmail(sender, password, recipient, subject, body, attachment);

                // 전송 결과에 따라 상태 표시
                if (success) {
                    statusLabel.setText("상태: 이메일 전송 성공!");
                } else {
                    statusLabel.setText("상태: 이메일 전송 실패!");
                }
            } catch (Exception ex) {
                statusLabel.setText("상태: 전송 오류 발생!");
                ex.printStackTrace();
            }

            // 상태를 초기화하기 위한 타이머
            Timer timer = new Timer(3000, e -> statusLabel.setText("상태: 대기 중"));
            timer.setRepeats(false); // 반복 방지
            timer.start(); // 타이머 시작
        });
    }
}
