package util;

import javax.mail.internet.MimeUtility;
public class Decoder {
    public static String decodeWithJavaMail(String encodedText) {
        try {
            return MimeUtility.decodeText(encodedText);
        } catch (Exception e) {
            return encodedText;
        }
    }
}