package edu.hartnell.iris.communication.text;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageReader;
import com.twilio.twiml.Say;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import edu.hartnell.iris.Iris;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TextManager extends HttpServlet {

    private final String ACCOUNT_SID;
    private final String AUTH_TOKEN;

    private final String MESSAGING_SERVICE_SID;


    private final TwilioRestClient client;

    public TextManager(String ACC_SID, String MES_SER_SID, String AUTH) {
        ACCOUNT_SID = ACC_SID;
        AUTH_TOKEN = AUTH;
        MESSAGING_SERVICE_SID = MES_SER_SID;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        client = Twilio.getRestClient();
        //new Thread(new textMonitor(ACCOUNT_SID)).start();
    }

    public void message(iText text) {
        Message.creator(new com.twilio.type.PhoneNumber(
                text.getRecipient()), MESSAGING_SERVICE_SID, text.getBody()
        ).create(client);
    }

    public void messageMedia(iText text, String url) {
        try {
            Message.creator(new com.twilio.type.PhoneNumber(
                    text.getRecipient()), MESSAGING_SERVICE_SID, text.getBody()
            ).setMediaUrl(url).create(client);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public class textMonitor implements Runnable {
        private String ACC_SID;
        private MessageReader reader;

        public textMonitor(String ACC_SID) {
            this.ACC_SID = ACC_SID;
            reader = Message.reader(ACC_SID);
        }

        @Override
        public void run() {
            Iris.report("Texts: " + reader.read(client).getLimit());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { e.printStackTrace(); }
            run();
        }
    }


}
