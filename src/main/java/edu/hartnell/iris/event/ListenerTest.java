package edu.hartnell.iris.event;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.event.events.EmailRecieved;

public class ListenerTest implements IrisListener {

    @IrisEvent
    public void recieveMessageEvent(EmailRecieved e) {
        Iris.say("THE LISTENER WORKS");
    }

    public void recievesEmailEvent(EmailRecieved e) {
        Iris.report("but shouldnt read this");
    }

}
