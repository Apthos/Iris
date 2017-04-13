package edu.hartnell.iris.event;

import edu.hartnell.iris.event.events.EmailReceive;

import java.awt.*;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public interface iEvent {

    class Registry {
        private static HashMap<iEvent.Events, Class> events = new HashMap<>();

        static {
            events.put(Events.EmailReceive, EmailReceive.class);
        }

        public static HashMap<iEvent.Events, Class> getClassMap() {
            return events;
        }

        public static boolean containsClass(Class<? extends iEvent> Class) {
            for (Events it : getClassMap().keySet()) {
                if (getClassMap().get(it).equals(Class)) return true;
            }
            return false;
        }


    }

    enum Events {
        EmailReceive;

        public static Class getEventClass(Events event) {
            return iEvent.Registry.getClassMap().get(event);
        }

        public static iEvent.Events getEventEnum(Class<? extends iEvent> C) {
            for (Events event : Registry.getClassMap().keySet()) {
                if (Registry.getClassMap().get(event).equals(C)) {
                    return event;
                }
            }
            return null;
        }

    }


}
