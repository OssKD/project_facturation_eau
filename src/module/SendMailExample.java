package module;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SendMailExample {

    public void sendRecoveryEmail(String destinataire, String title, String mes) {
    	final String username = "osskd096@gmail.com";
        final String password = "wypg jtbr yaju gemp"; // Remplacez par votre mot de passe

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(title);
            message.setText(mes);

            Transport.send(message);
            System.out.println("Email de confirmation envoyé !");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}