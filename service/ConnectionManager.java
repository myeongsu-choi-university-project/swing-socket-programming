package service;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class ConnectionManager {
    private static ConnectionManager instance;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int tagCounter = 0;

    private static final String HOST = "imap.naver.com";
    private static final int PORT = 993;

    private ConnectionManager() { }

    // ConnectionManager 인스턴스를 반환하는 메서드
    public static ConnectionManager getInstance() {
        if(instance == null)
            instance = new ConnectionManager();
        return instance;
    }

    // 서버와 연결을 설정하는 메서드
    public void connect() throws IOException {
        // 소켓이 null이거나 닫혀있는 경우에만 연결 설정
        if(socket == null || socket.isClosed()) {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) socketFactory.createSocket(HOST, PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            readResponse();   // 초기 연결 시 서버 응답 읽기
            startHeartbeat(); // 연결 유지용 하트비트 시작
        }
    }

    // 서버 타임아웃 방지용 메서드
    public void startHeartbeat() {
        Thread heartbeatThread = new Thread(() -> {
            try {
                while(!socket.isClosed()) {
                    // NOOP 명령 전송하여 연결 유지
                    sendCommand("a" + getTagCounter() + " NOOP");
                    readResponse();
                    Thread.sleep(30000);    // 30초 대기 (서버 타임아웃에 맞춰 조정 가능)
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Heartbeat thread interrupted: " + e.getMessage());
            }
        });
        heartbeatThread.setDaemon(true);    // 데몬 스레드로 설정 (JVM 종료 시 자동 종료)
        heartbeatThread.start();            // 하트비트 스레드 시작
    }

    // 서버 응답을 읽고 콘솔에 출력하는 메서드
    public String readResponse() throws IOException {
        String response = reader.readLine();
        System.out.println("S: " + response);
        return response;
    }

    // 서버로 명령을 전송하는 메서드
    public void sendCommand(String command) throws IOException {
        writer.write(command + "\r\n");
        writer.flush();
        System.out.println("C: " + command);
    }

    // 서버와의 연결을 종료하는 메서드
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            // 로그아웃 명령 전송
            sendCommand("a" + tagCounter + " LOGOUT");
            readResponse(); // 로그아웃 응답 읽기
            socket.close(); // 소켓 닫기
            reader.close(); // 스트림 닫기
            writer.close(); // 스트림 닫기
        }
    }

    // 현재 태크 번호를 반환하는 메서드
    public int getTagCounter() {
        return tagCounter;
    }

    // 태그 번호를 설정하는 메서드
    public void setTagCounter(int tagCounter) {
        this.tagCounter = tagCounter;
    }
}
