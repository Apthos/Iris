package edu.hartnell.iris.email;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.plugin.Plugin;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class iMail {

    public enum RecipientType {
        TO, CC, BCC;

        protected Message.RecipientType toMimeRecipientType(){
            switch (this){
                case TO:
                    return MimeMessage.RecipientType.TO;
                case CC:
                    return MimeMessage.RecipientType.CC;
                case BCC:
                    return MimeMessage.RecipientType.BCC;
            }
            return null;
        }
    }

    private Plugin plugin;
    private String text = "", subject = "";

    private MimeMessage email;
    private HashMap<String, Message.RecipientType> recipients = new HashMap<>();
    private List<File> files = new ArrayList<>();

    public iMail(Plugin plugin){
        this.plugin = plugin;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public void addRecipient(String Address, iMail.RecipientType type){
        recipients.put(Address, type.toMimeRecipientType());
    }

    public void addFile(File file){
        files.add(file);
    }

    public void send(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements){
            Iris.report(stackTraceElement.getMethodName() + " : " +
            stackTraceElement.getLineNumber());
        }
        new Thread(() -> {
            try {
                if (! Iris.getEmailManager().connected) {
                    Iris.getEmailManager().addToQueue(this);
                    Iris.warn("EmailManager not connected adding to queue!");
                    return;
                }

                if (recipients.isEmpty() || subject == "" || text == "") {
                    Iris.report(plugin.getName() + " could not send email!");
                    return;
                }

                Multipart multipart = new MimeMultipart();

                email = Iris.getEmailManager().getMimeMessage();
                email.setFrom(new InternetAddress(Iris.getEmailManager().getUser()));
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(text);
                multipart.addBodyPart(messageBodyPart);
                email.setSubject(subject);

                for (String recipient : recipients.keySet()) {
                    email.addRecipient(recipients.get(recipient),
                            InternetAddress.parse(recipient)[0]);
                }

                List<MimeBodyPart> attachments = new ArrayList<>();
                for (File file : files){
                    MimeBodyPart attachPart = new MimeBodyPart();
                    attachPart.attachFile(file);
                    attachments.add(attachPart);
                }

                for (MimeBodyPart part : attachments){
                    multipart.addBodyPart(part);
                }
                email.setContent(multipart);

                Transport.send(email);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }).start();
    }

}
