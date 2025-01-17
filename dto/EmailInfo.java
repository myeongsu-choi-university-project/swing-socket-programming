package dto;

public class EmailInfo {
    private String from;
    private String to;
    private String date;
    private String subject;

    public EmailInfo() {}

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "EmailInfo{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", date='" + date + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
