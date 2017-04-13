package edu.hartnell.iris.event;

import edu.hartnell.iris.event.events.EmailReceive;

import java.util.HashMap;

public interface iListener {

    class Registry {

    }


    enum Listener {
        EmailReceive;

//        public static EventHandler byValue(Class<? extends EventHandler> tClass){
//            return iEvent.Registry.getClassMap().get(listener);
//        }
    }

}
