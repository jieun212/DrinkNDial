package edu.uw.tacoma.team8.drinkndial.confirm;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This class constructs a GMailSender object as well as
 * providing a method that allows our object to call that method to send an email from
 * the GMailSender to a recipient.
 *
 * @author Lovejit Hari
 * @version 3/9/2017
 */
public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new edu.uw.tacoma.team8.drinkndial.confirm.JSSEProvider());
    }

    /**
     * Constructs a GMailSender object that initializes the user and password fields.
     * Also we set the properties that coincide with Google's security settings in order to be
     * able to send mail from the host mail to the user.
     *
     * @param user Whoever is sending the email
     * @param password password of the email
     */
    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    /**
     * Sends an email to the recipient from a sender
     *
     *
     * @param subject Title of the email
     * @param body Content of the email
     * @param sender Whoever sent the email
     * @param recipients Recipient of the email
     * @throws Exception e
     */
    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try{
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            } else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            }
            Transport.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This class produces a connection object that will automatically participate in
     * connection pooling.
     */
    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        /**
         * Constructs an object that initializes the data and the string type
         * @param data byte array
         * @param type string
         */
        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}