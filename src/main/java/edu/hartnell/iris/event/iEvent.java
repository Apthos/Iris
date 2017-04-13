package edu.hartnell.iris.event;

import edu.hartnell.iris.event.events.EmailReceive;

import java.util.HashMap;

public interface iEvent {

    class Registry {
        private static HashMap<Class, iListener.Listener> events = new HashMap<>();

        static {
            events.put(EmailReceive.class, iListener.Listener.EmailReceive);
        }

        public static HashMap<Class, iListener.Listener> getClassMap() {
            return events;
        }

        //public  getEventEnum

    }

    enum Listener {
        EmailReceive;

//        public static EventHandler byValue(Class<? extends EventHandler> tClass){
//            return iEvent.Registry.getClassMap().get(listener);
//        }
    }


}
