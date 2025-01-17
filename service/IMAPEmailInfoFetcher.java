package service;

import dto.EmailInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.Decoder.decodeWithJavaMail;

public class IMAPEmailInfoFetcher {
    private ConnectionManager connectionManager;
    public IMAPEmailInfoFetcher(String boxType) {
        this.connectionManager = ConnectionManager.getInstance();
    }

    // 메일의 발신자 이메일, 수신 날짜, 메일 제목, 메일 내용 가져오기
    public List<EmailInfo> fetchEmailInfo(
            String boxType, int page, int emailsPerPage
    ) throws IOException {
        List<EmailInfo> emailInfoList = new ArrayList<>();

        connectionManager.connect();
        selectMailBox(boxType);

        List<String> emailIDs = fetchEamilIDs(page, emailsPerPage);
        String headerFields = determineHeaderFields(boxType);
        for (String emailID : emailIDs) {
            EmailInfo emailInfo = fetchEmailDetails(emailID, headerFields);
            emailInfoList.add(emailInfo);
        }

        return emailInfoList;
    }

    // 메일함 선택
    // EX) INBOX는 받은 메일함, Sent Messages는 보낸 메일함
    private void selectMailBox(String boxType) throws IOException {
        connectionManager.sendCommand(
                "a" + connectionManager.getTagCounter() + " SELECT " + boxType);
        connectionManager.readResponse();
        connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);
    }
    
    // 이메일 ID 목록 조회
    private List<String> fetchEamilIDs(int page, int emailsPerPage) throws IOException {
        connectionManager.sendCommand(
                "a" + connectionManager.getTagCounter() + " SEARCH ALL");

        String line;
        List<String> emailIDs = new ArrayList<>();
        while((line = connectionManager.readResponse()) != null) {
            if(line.contains(connectionManager.getTagCounter() + " OK")) {
                break;
            }
            if (line.startsWith("* SEARCH")) {
                String[] ids = line.substring(9).trim().split(" ");
                Collections.addAll(emailIDs, ids);
            }
        }
        Collections.reverse(emailIDs);
        System.out.println("emailIDs = " + emailIDs);

        int startIdx = page * emailsPerPage;
        int endIdx = Math.min(startIdx + emailsPerPage, emailIDs.size());
        System.out.println("emailIDs = " + emailIDs.subList(startIdx, endIdx));

        connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);

        return emailIDs.subList(startIdx, endIdx);
    }

    // 메일함에 따라 불러올 헤더 필드 정보 설정
    private String determineHeaderFields(String boxType) {
        switch (boxType.toUpperCase()) {
            case "INBOX":
                return "FROM DATE SUBJECT";
            case "\"SENT MESSAGES\"":
                return "TO DATE SUBEJCT";
            case "TRASH":
                return "FROM TO DATE SUBJECT";
            default:
                return "FROM DATE SUBJECT";
        }
    }

    // 이메일 상세 정보 가져오기 + 디코딩 추가
    private EmailInfo fetchEmailDetails(String emailID, String headerFields) throws IOException {
        connectionManager.sendCommand(
                "a" + connectionManager.getTagCounter() + " FETCH " + emailID + " (BODY[HEADER.FIELDS (" + headerFields + ")])");

        String line;
        StringBuilder encodedFromBuilder = new StringBuilder();
        StringBuilder encodedToBuilder = new StringBuilder();
        StringBuilder encodedSubjectBuilder = new StringBuilder();
        String date = "";
        while((line = connectionManager.readResponse()) != null) {
            if (line.contains("a" + connectionManager.getTagCounter() + " OK")) {
                break;
            }

            if (line.startsWith("FROM:"))
                encodedFromBuilder.append(line.substring(5).trim()).append(" ");
            else if (line.startsWith("TO:"))
                encodedToBuilder.append(line.substring(3).trim()).append(" ");
            else if (line.startsWith("DATE:"))
                date = line.substring(5).trim();
            else if (line.startsWith("SUBJECT:"))
                encodedSubjectBuilder.append(line.substring(8).trim()).append(" ");
        }

        String decodedFrom = decodeEmailAddress(encodedFromBuilder.toString().trim());
        String decodedTo = decodeWithJavaMail(encodedToBuilder.toString().trim());
        String decodedSubject = decodeWithJavaMail(encodedSubjectBuilder.toString().trim());
        String decodedDate = decodeWithJavaMail(date);

        System.out.println("\n** 메일 헤더 정보(디코딩 후) ================================");
        if (!decodedFrom.isEmpty()) System.out.println("From: " + decodedFrom);
        if (!decodedTo.isEmpty()) System.out.println("To: " + decodedTo);
        System.out.println("Date: " + decodedDate);
        if (!decodedSubject.isEmpty()) System.out.println("Subject: " + decodedSubject);
        System.out.println("==========================================================\n");

        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setFrom(decodedFrom);
        emailInfo.setTo(decodedTo);
        emailInfo.setDate(decodedDate);
        emailInfo.setSubject(decodedSubject);

        connectionManager.setTagCounter(connectionManager.getTagCounter() + 1);

        return emailInfo;
    }

    private String decodeEmailAddress(String encodedFrom) {
        // 이름과 이메일 주소 분리
        Pattern pattern = Pattern.compile("\"(.*?)\"\\s*<(.+?)>");
        Matcher matcher = pattern.matcher(encodedFrom);

        if (matcher.find()) {
            String encodedName = matcher.group(1);  // 인코딩된 이름
            String email = matcher.group(2);        // 이메일 주소

            // 이름만 디코딩
            String decodedName = decodeWithJavaMail(encodedName);
            return "\"" + decodedName + "\" <" + email + ">";
        } else {
            // 인코딩된 부분이 없으면 전체 디코딩 시도
            return decodeWithJavaMail(encodedFrom);
        }
    }
}
