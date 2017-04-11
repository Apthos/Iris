package edu.hartnell.iris.text;

import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.Account;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.trunking.v1.trunk.PhoneNumber;
import edu.hartnell.iris.Iris;

import java.util.HashMap;

public class TextManager {

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
    }

    public void message(String cell, String messageBody){
        Message message = Message.creator(new com.twilio.type.PhoneNumber(cell),
                MESSAGING_SERVICE_SID, messageBody).create(client);
    }


}
