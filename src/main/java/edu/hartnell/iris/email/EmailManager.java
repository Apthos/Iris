package edu.hartnell.iris.email;

import edu.hartnell.iris.Iris;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import java.util.LinkedList;
import java.util.Properties;

public class EmailManager {

    public boolean connected = false;

    private Store mStore;
    private Session eSession;
    private String user, pass;
    private boolean initialized = true;

    private LinkedList<iMail> mailQueue = new LinkedList<>();

    public EmailManager(String user, String pass){
        this.user = user; this.pass = pass;

        Iris.warn("Logining into " + getUser());
        if (initialized == false){
            Iris.report("EmailManager Failed to initialize!");
            return;
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        try {
            eSession = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, pass);
                        }
                    }
            );

            mStore = eSession.getStore("pop3s");
            mStore.connect("pop.gmail.com", user, pass);
        } catch (MessagingException e){
            e.printStackTrace();
            initialized = false;
        }
        if (!initialized) {
            connected = false;
            return;
        }
        connected = true;
        workQueue();
    }

    public void addToQueue(iMail mail){
        this.mailQueue.add(mail);
    }

    private void workQueue() {
        for (iMail mail : mailQueue)
            mail.send();
    }

    public MimeMessage getMimeMessage(){
        return new MimeMessage(eSession);
    }

    public String getUser(){
        return user;
    }

}
