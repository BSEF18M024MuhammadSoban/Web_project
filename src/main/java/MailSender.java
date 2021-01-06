import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MailSender {
    private static String MAIL_HOST;
    private static String MAIL_PORT;
    private static String MAIL_USERNAME;
    private static String MAIL_PASSWORD;
    public static void initMailSender(String host, String port, String uname, String pass){
        MAIL_HOST=host;
        MAIL_PORT =port;
        MAIL_USERNAME=uname;
        MAIL_PASSWORD=pass;
    }
    public static Boolean sendMail(String to, String sub, String msg){
        Properties props = new Properties();
        props.put("mail.smtp.host", MAIL_HOST);
        //props.put("mail.smtp.port", MAIL_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props,new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(MAIL_USERNAME,MAIL_PASSWORD);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL_USERNAME));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(sub);
            message.setText(msg);
            Transport.send(message);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}
