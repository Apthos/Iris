package edu.hartnell.iris.communication.text;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.plugin.IrisFrame;

public class iText {

    private String recipient;
    private String body;

    public iText(IrisFrame frame) {}

    public void setRecipient(String Phone) {
        recipient = Phone;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getBody() {
        return body;
    }

    public void send() {
        Iris.getTextManager().message(this);
    }


}
