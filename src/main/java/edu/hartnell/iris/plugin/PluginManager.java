package edu.hartnell.iris.plugin;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.event.IrisEvent;
import edu.hartnell.iris.event.IrisListener;
import edu.hartnell.iris.event.events.EmailRecieved;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;

import static java.lang.Thread.sleep;

public class PluginManager extends ClassLoader{

    private LinkedHashSet<Plugin> plugins = new LinkedHashSet<>();

    public PluginManager(){
        Iris.say("Starting Plugin Manager!");
        File rdir;
        try {
            rdir = Iris.getRuntimeLocation();
            Iris.say("Runtime Parent Directory: " + rdir.getAbsolutePath());
        } catch (URISyntaxException e) { e.printStackTrace(); return; }

        File pdir = new File(rdir + "/Plugins");
        if (!pdir.exists()){
            Iris.warn("Warning: Could not find plugin folder, Generating new one!");
            pdir.mkdir();
        }

        new Thread(() -> {
            try { sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
            for (File f : pdir.listFiles()) {
                if (f.getName().endsWith(".jar")) {
                    try {
                        Plugin plugin = new Plugin(f);
                        if (plugin.isInitialized())
                            Iris.say("Plugin");
                        plugins.add(plugin);
                        plugin.enable();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }).start();
    }

    public Plugin getPluginFromInstance(IrisFrame frame){
        for (Plugin plugin : plugins){
            if (plugin.getInstance().equals(frame)){
                return plugin;
            }
        }
        return null;
    }

    public void registerListener(IrisListener e) {
        Method[] methods = e.getClass().getMethods();
        for (Method method : methods) {
            boolean anEvent = false;
            String Parameters = "";
            String Annotations = "";

            method.setAccessible(true);

            if (method.getParameters().length == 1 &&
                    method.getParameters()[0].getType().equals(EmailRecieved.class) &&
                    method.isAnnotationPresent(IrisEvent.class)) {
                anEvent = true;
            } else {
                for (Parameter para : method.getParameters()) {
                    Parameters += para.getType().getSimpleName() + " ";
                }
                for (Annotation annotation : method.getAnnotations()) {
                    Annotations += annotation.annotationType().getSimpleName();
                }
                Parameters = "pLength: " + method.getParameters().length + " Parameters: " +
                        Parameters + " Annotations: " + Annotations;
            }

            if (anEvent) {
                Iris.report("Method/ Name:" + method.getName() + " [ " + Parameters + " ] ");
                method.setAccessible(true);
                try {
                    method.invoke(e, new EmailRecieved());
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } else {
                Iris.say("Method/ Name:" + method.getName() + " [ " + Parameters + " ] ");
            }

        }
    }

}
