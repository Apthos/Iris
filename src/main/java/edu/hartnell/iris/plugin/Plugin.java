package edu.hartnell.iris.plugin;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Plugin {

    public enum PermissionGroup{
        ADMIN, STANDARD, RESTRICTED;

        public static PermissionGroup valueOfString(String string){
            switch (string.toLowerCase()){
                case "admin":
                    return PermissionGroup.ADMIN;
                case "restricted":
                    return PermissionGroup.RESTRICTED;
                default:
                    return PermissionGroup.STANDARD;
            }
        }
    }

    private final IrisFrame PLUGIN_INSTANCE;
    private final String NAME;
    private final String AUTHOR;
    private final PermissionGroup GROUP;
    private final String VERSION;

    private boolean initialized = false;

    public Plugin(IrisFrame irisFrame, String Name, String Author,
                  String Group, String Version){
        PLUGIN_INSTANCE = irisFrame;
        this.NAME = Name;
        this.AUTHOR = Author;
        this.GROUP = PermissionGroup.valueOfString(Group);
        this.VERSION = Version;
    }

    public Plugin(File f) throws Exception {
        URLClassLoader classLoader = URLClassLoader.newInstance
                (new URL[] { f.toURL() });
        JarFile pJar = new JarFile(f);
        JarEntry entry = pJar.getJarEntry("Info.xml");
        BufferedReader inputReader = new BufferedReader
                (new InputStreamReader(pJar.getInputStream(entry)));
        StringBuilder sb = new StringBuilder();
        String inline = "";
        while ((inline = inputReader.readLine()) != null) {
            sb.append(inline);
        }
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build
                (new ByteArrayInputStream(sb.toString().getBytes()));
        Element infoNode = doc.getRootElement().getChild("Info");
        String mainClass = infoNode.getChildText("Class"),
                name = infoNode.getChildText("Name"),
                author = infoNode.getChildText("Author"),
                group = infoNode.getChildText("Group"),
                version = infoNode.getChildText("Version");
        IrisFrame instance =
                (IrisFrame) classLoader.loadClass
                        (mainClass).newInstance();
        PLUGIN_INSTANCE = instance;
        this.NAME = name;
        this.AUTHOR = author;
        this.GROUP = PermissionGroup.valueOfString(group);
        this.VERSION = version;
        initialized = true;
    }

    public boolean enable(){
        PLUGIN_INSTANCE.onEnable();
        return true;
    }

    public boolean disable(){
        PLUGIN_INSTANCE.onDisable();
        return true;
    }

    public boolean reload(){
        PLUGIN_INSTANCE.onReload();
        return true;
    }

    public String getName(){
        return NAME;
    }

    public String getAuthor(){
        return AUTHOR;
    }

    public PermissionGroup getPermissionGroup(){
        return GROUP;
    }

    public String getVersion(){
        return VERSION;
    }

    public IrisFrame getInstance() {
        return PLUGIN_INSTANCE;
    }

    public boolean isInitialized(){
        return initialized;
    }

}
