package edu.hartnell.iris.test;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.event.EventHandler;
import edu.hartnell.iris.event.iListener;
import edu.hartnell.iris.event.events.EmailReceive;

public class ListenerTest implements iListener {

    @EventHandler
    public void recieveMessageEvent(EmailReceive e) {
        Iris.say("THE LISTENER WORKS");
    }


}
