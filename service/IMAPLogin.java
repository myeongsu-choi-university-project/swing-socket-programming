package service;

import java.io.IOException;

public class IMAPLogin {
    private String email;
    private String password;
    private ConnectionManager connectionManager;

    public IMAPLogin(String email, String password) {
        this.email = email;
        this.password = password;
        this.connectionManager = ConnectionManager.getInstance();
    }

    // 서버에 LOGIN 명령을 보내고 응답을 확인하여 인증 상태를 반환하는 메서드
    public boolean login() throws IOException {
        // 서버와 연결 시도
        connectionManager.connect();

        // LOGIN 명령 전송 (예: a0 LOGIN user@example.com password123)
        connectionManager.sendCommand(
                "a" + connectionManager.getTagCounter() + " LOGIN " + email + " " + password
        );

        // 서버 응답 읽기
        String responseLine;
        while((responseLine = connectionManager.readResponse()) != null) {
            // 응답에 "OK"와 "Logged in"이 포함된 경우 로그인 성공
            if(responseLine.contains("OK") && responseLine.contains("Logged in")) {
                System.out.println("로그인 성공");

                // 태크 카운터 증가 (다음 명령 준비)
                connectionManager.setTagCounter(
                        connectionManager.getTagCounter() + 1
                );

                return true;
            }
            // 응답에 "NO" 또는 "BAD"가 포함된 경우 로그인 실패
            else if(responseLine.contains("NO") || responseLine.contains("BAD")) {
                System.out.println("로그인 실패");
                return false;
            }
        }
        return false;
    }
}
