package edu.hartnell.iris.plugin;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.hartnell.iris.Iris;
import edu.hartnell.iris.event.EventHandler;
import edu.hartnell.iris.event.iListener;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;

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
                sleep(1000);
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

    private Table<EventHandler, iListener, Method> listeners = HashBasedTable.create();

    public void registerListener(iListener listen) {
        Method[] methods = listen.getClass().getMethods();
        for (Method method : methods) {

            method.setAccessible(true);

//            if (! (method.getParameters().length == 1 &&
//                    method.getParameters()[0].getType().get &&
//                    method.isAnnotationPresent(EventHandler.class))) continue;

//            listeners.put(iListener.Listener.byValue((Class<? extends EventHandler>)
//                    method.getParameters()[0].getType()), listen, method);



        }
    }

}
