package service;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * SMTPSender 클래스 : 네이버 SMTP 서버를 사용하여 이메일 전송
 * SMTP 프로토콜을 직접 구현하여 첨부 파일 지원
 */
public class SMTPSender {
    private static final String SMTP_HOST = "smtp.naver.com";
    private static final int SMTP_PORT = 587;

    /**
     * 이메일 전송 메서드
     *
     * @param senderEmail    발신자 이메일
     * @param senderPassword 발신자 비밀번호 (앱 비밀번호 사용 권장)
     * @param recipientEmail 수신자 이메일
     * @param subject        이메일 제목
     * @param body           이메일 본문
     * @param attachmentPath 첨부 파일 경로 (없으면 null)
     * @return 전송 성공 여부
     */
    public static boolean sendEmail(
            String senderEmail, String senderPassword, String recipientEmail,
            String subject, String body, String attachmentPath
    ) {
        try (Socket socket = new Socket(SMTP_HOST, SMTP_PORT)) {
            // 1. 서버 연결 및 입력/출력 스트림 생성
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 2. 서버 응답 읽기
            readResponse(reader);

            // 3. EHLO: SMTP 서버와의 초기화
            sendCommand(writer, "EHLO " + SMTP_HOST);
            readResponse(reader);

            // 4. STARTTLS: 보안 연결 시작
            sendCommand(writer, "STARTTLS");
            readResponse(reader);

            // 기존 소켓을 닫고 보안 소켓으로 전환
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, SMTP_HOST, SMTP_PORT, true);

            // SSL 핸드셰이크 시작
            sslSocket.startHandshake();

            // 새 SSL 소켓의 스트림 재생성
            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream()));

            // 5. 인증: 로그인 처리
            sendCommand(writer, "AUTH LOGIN");
            readResponse(reader);

            sendCommand(writer, Base64.getEncoder().encodeToString(senderEmail.getBytes()));
            readResponse(reader);

            sendCommand(writer, Base64.getEncoder().encodeToString(senderPassword.getBytes()));
            readResponse(reader);

            // 6. 이메일 송신 정보 설정
            sendCommand(writer, "MAIL FROM:<" + senderEmail + ">");
            readResponse(reader);

            sendCommand(writer, "RCPT TO:<" + recipientEmail + ">");
            readResponse(reader);

            sendCommand(writer, "DATA");
            readResponse(reader);

            // 7. 이메일 데이터 작성
            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append("From: ").append(senderEmail).append("\r\n");
            dataBuilder.append("To: ").append(recipientEmail).append("\r\n");
            dataBuilder.append("Subject: ").append(subject).append("\r\n");

            // 첨부 파일 여부 확인
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                // 멀티파트 형식으로 데이터 작성
                String boundary = "=====BOUNDARY====="; // 고유 경계선
                dataBuilder.append("MIME-Version: 1.0\r\n");
                dataBuilder.append("Content-Type: multipart/mixed; boundary=" + boundary + "\r\n\r\n");

                // 본문 추가
                dataBuilder.append("--").append(boundary).append("\r\n");
                dataBuilder.append("Content-Type: text/plain; charset=utf-8\r\n");
                dataBuilder.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
                dataBuilder.append(body).append("\r\n\r\n");

                // 첨부 파일 추가
                File file = new File(attachmentPath);
                String fileName = file.getName();
                String fileContent = encodeFileToBase64(file);

                dataBuilder.append("--").append(boundary).append("\r\n");
                dataBuilder.append("Content-Type: application/octet-stream; name=\"" + fileName + "\"\r\n");
                dataBuilder.append("Content-Transfer-Encoding: base64\r\n");
                dataBuilder.append("Content-Disposition: attachment; filename=\"" + fileName + "\"\r\n\r\n");
                dataBuilder.append(fileContent).append("\r\n\r\n");
                dataBuilder.append("--").append(boundary).append("--\r\n");
            } else {
                // 단순 텍스트 이메일
                dataBuilder.append("Content-Type: text/plain; charset=utf-8\r\n");
                dataBuilder.append("Content-Transfer-Encoding: 7bit\r\n\r\n");
                dataBuilder.append(body).append("\r\n");
            }

            // 이메일 끝 표시
            dataBuilder.append(".\r\n");

            // 이메일 데이터 전송
            sendCommand(writer, dataBuilder.toString());
            readResponse(reader);

            // 8. 이메일 전송 완료 및 연결 종료
            sendCommand(writer, "QUIT");
            readResponse(reader);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 서버에 명령어를 전송
     *
     * @param writer BufferedWriter 객체
     * @param command 전송할 명령어
     * @throws IOException 전송 오류 시
     */
    private static void sendCommand(
            BufferedWriter writer, String command
    ) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("C: " + command);
    }

    /**
     * 서버 응답 읽기
     *
     * @param reader BufferedReader 객체
     * @throws IOException 읽기 오류 시
     */
    private static void readResponse(
            BufferedReader reader
    ) throws IOException {
        String response;
        while((response = reader.readLine()) != null) {
            System.out.println("S: " + response);

            // 응답이 여러 줄인 경우 마지막 줄은 코드 뒤에 공백이 있음
            if (response.matches("\\d{3} .+"))
                break;
        }
    }

    private static String encodeFileToBase64(
            File file
    ) throws IOException {
        byte[] fileBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBytes);
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }
}