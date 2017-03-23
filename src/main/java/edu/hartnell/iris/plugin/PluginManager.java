package edu.hartnell.iris.plugin;

import edu.hartnell.iris.Iris;

import java.io.*;
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

}
