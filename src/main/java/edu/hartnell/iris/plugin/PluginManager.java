package edu.hartnell.iris.plugin;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.hartnell.iris.Iris;
import edu.hartnell.iris.event.EventHandler;
import edu.hartnell.iris.event.iEvent;
import edu.hartnell.iris.event.iListener;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class PluginManager extends ClassLoader {

    private LinkedHashSet<Plugin> plugins = new LinkedHashSet<>();

    public PluginManager() {
        Iris.say("Starting Plugin Manager!");
        File rdir;
        try {
            rdir = Iris.getRuntimeLocation();
            Iris.say("Runtime Parent Directory: " + rdir.getAbsolutePath());
        } catch (URISyntaxException e) { e.printStackTrace(); return; }

        File pdir = new File(rdir + "/Plugins");
        if (! pdir.exists()) {
            Iris.warn("Warning: Could not find plugin folder, Generating new one!");
            pdir.mkdir();
        }

        new Thread(() -> {
            try {
                sleep(100);
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

    public Plugin getPluginFromInstance(IrisFrame frame) {
        for (Plugin plugin : plugins) {
            if (plugin.getInstance().equals(frame)) {
                return plugin;
            }
        }
        return null;
    }

    public boolean isMainClass(Class C) {
        return C.isAssignableFrom(IrisFrame.class);
    }

    private Table<iEvent.Events, iListener, Method> listeners = HashBasedTable.create();

    public void registerListener(iListener listen) {
        Method[] methods = listen.getClass().getMethods();
        for (Method method : methods) {
            method.setAccessible(true);

            if (! (method.getParameters().length == 1 &&
                    iEvent.Registry.containsClass((Class<? extends iEvent>)
                            method.getParameters()[0].getType()) &&
                    method.isAnnotationPresent(EventHandler.class))) continue;

            listeners.put(iEvent.Events.getEventEnum((Class<? extends iEvent>)
                    method.getParameters()[0].getType()), listen, method);
        }
    }

    public void invoke(iEvent.Events event, iEvent passIn) {
        if (! iEvent.Events.getEventEnum(passIn.getClass()).equals(event)) {
            Iris.report("could not invoke!");
            // todo: Add stack trace error!
            return;
        }

        Map<iListener, Method> map = listeners.row(event);

        for (iListener key : map.keySet()) {
            Method method = map.get(key);
            try {
                method.invoke(key, passIn);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }



}
