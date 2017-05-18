package edu.hartnell.iris;

import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.communication.socket.SocketManager;
import edu.hartnell.iris.data.DataManager;
import edu.hartnell.iris.communication.email.EmailManager;
import edu.hartnell.iris.gui.Console;
import edu.hartnell.iris.plugin.PluginManager;
import edu.hartnell.iris.communication.text.TextManager;
import edu.hartnell.iris.utility.ResourceUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Iris {

    private static Console console = null;
    private static CommandManager commandManager = null;
    private static PluginManager pluginManager = null;
    private static EmailManager emailManager = null;
    private static TextManager textManager = null;
    private static DataManager dataManager = null;
    private static SocketManager socketManager = null;

    public static void main(String... args) {
        console = new Console();
        setup();
    }

    private static void setup() {
        Settings conf = collectCredentials();
        commandManager = new CommandManager();
        console.initializeEvents(console, commandManager);
        try {
            conf.select("email");
            if (conf.getBoolean("enabled")) {
                Iris.warn("Iris is running with emails enabled!");
                emailManager = new EmailManager
                        (conf.getString("USER"), conf.getString("PASS"));
            } else {
                Iris.warn("Iris is running with emails disabled!");
            }
        } catch (Exception e) {
            Iris.report("Email Module could not be initialized");
        }
        try {
            conf.select("twil");
            if (conf.getBoolean("enabled")) {
            Iris.warn("Iris is running with texts enabled!");
                textManager = new TextManager(conf.getString("ACCOUNT_SID"),
                        conf.getString("MSG_SER_SID"), conf.getString("AUTH_TOKEN"));
        } else {
            Iris.warn("Iris is running with texts disabled!");
        }
        } catch (Exception e) {
            Iris.report("Text Module could not be initialized");
        }
        try {
            conf.select("mysql");
            if (conf.getBoolean("enabled")) {
                Iris.warn("Iris is running with databases enabled!");
                dataManager = new DataManager(conf.getString("HOST"), conf.getString("PORT"),
                        conf.getString("USER"), conf.getString("PASS"));
            } else {
                Iris.warn("Iris is running with databases disabled!");
            }
        } catch (Exception e) {
            Iris.report("Database Module could not be initialized");
        }

        try {
            socketManager = new SocketManager(conf.select("socket").getInt("port"),
                    conf.getInt("timeout"), conf.getInt("concurrent"));
        } catch (Exception e) {
            Iris.report("Socket Module could not be initialized");
        }

        pluginManager = new PluginManager(); //has to be last
    }

    public static void reload() {
        commandManager.clear();
        pluginManager.clear();
        socketManager.kill();
        setup();
        System.gc();
    }

    public static void say(String Message) {
        console.say(Message);
    }

    public static void respond(String Message) {
        console.respond(Message);
    }

    public static void warn(String Warning) {
        console.warn(Warning);
    }

    public static void report(String Report) {
        console.report(Report);
    }

    public static Console getConsole() {
        return console;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static EmailManager getEmailManager() {
        if (emailManager == null) {
            Iris.report("A null version of Email manager has been retrieved!");
        }
        return emailManager;
    }

    public static DataManager getDataManager() {
        if (dataManager == null) {
            Iris.report("A null version of Data manager has been retrieved!");
        }
        return dataManager;
    }

    public static TextManager getTextManager() {
        if (textManager == null){
            Iris.report("A null version of Text Manager has been retrieved!");
        }
        return textManager;
    }

    public static SocketManager getSocketManager() {
        if (socketManager == null) {
            Iris.report("A null version of Socket Manager has been retrieved!");
        }
        return socketManager;
    }

    public static File getRuntimeLocation() throws URISyntaxException {
        File file = new File(Iris.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI().getPath());
        return file.getParentFile();
    }

    public static File getRuntimeJar() throws URISyntaxException {
        File file = new File(Iris.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI().getPath());
        return file;
    }

    private static class Settings {

        private HashMap<String, HashMap<String, Object>> settings = new HashMap<>();
        private String section = null;

        public void add(String setting, Object value) {
            if (section == null) {
                Iris.report("Could not add because current section not selected!");
                return;
            }
            settings.get(section).put(setting, value);
        }

        public Settings select(String index) {
            if (! settings.keySet().contains(index.toLowerCase())) {
                Iris.report("Could not select this section because it doesn't exist!");
                return this;
            }
            section = index.toLowerCase();
            return this;
        }

        public Settings createSection(String title) {
            settings.put(title, new HashMap<>());
            section = title.toLowerCase();
            return this;
        }

        public String getString(String setting) {
            return (String) settings.get(section).get(setting);
        }

        public int getInt(String setting) {
            return (int) settings.get(section).get(setting);
        }

        public boolean getBoolean(String setting) {
            return (boolean) settings.get(section).get(setting);
        }

        public Object getObject(String setting) {
            return settings.get(section).get(setting);
        }


    }

    private static Settings collectCredentials() {
        Settings settings = new Settings();
        try {
            File cDir = new File(Iris.getRuntimeLocation() + "/Credentials");
            File EMailFile = new File(cDir + "/EMail.xml");
            File MySQLFile = new File(cDir + "/MySQL.xml");
            File TwilFile = new File(cDir + "/Twil.xml");
            File configFile = new File(cDir + "/Config.xml");

            SAXBuilder builder = new SAXBuilder();

            if (! cDir.exists()) {
                Iris.warn("Warning: Could not find credentials folder, Generating new one!");
                cDir.mkdir();
                Iris.warn("Copying login.xml to credentials folder!");
            }

            if (! EMailFile.exists()) {
                Iris.warn("Warning: Could not find EMail file, Generating new one!");
                ResourceUtils.ExportResource("/EMail.xml", "Credentials");
            }

            Document EMail = builder.build(EMailFile);
            settings.createSection("email");
            settings.add("enabled", (EMail.getRootElement().getAttributeValue("enabled")
                    .equalsIgnoreCase("true")));
            settings.add("USER", EMail.getRootElement().getChild("email")
                    .getChildText("address"));
            settings.add("PASS", EMail.getRootElement().getChild("email")
                    .getChildText("password"));

            if (! MySQLFile.exists()) {
                Iris.warn("Warning: Could not find MySQL file, Generating new one!");
                ResourceUtils.ExportResource("/MySQL.xml", "Credentials");
            }
            Document mysql = builder.build(MySQLFile);
            settings.createSection("mysql");
            settings.add("enabled", (mysql.getRootElement().getAttributeValue("enabled")
                    .equalsIgnoreCase("true")));
            settings.add("HOST", mysql.getRootElement().getChildText("host"));
            settings.add("PORT", mysql.getRootElement().getChildText("port"));
            settings.add("USER", mysql.getRootElement().getChildText("user"));
            settings.add("PASS", mysql.getRootElement().getChildText("pass"));

            if (! TwilFile.exists()) {
                Iris.warn("Warning: Could not find Twilio login file, Generating new one!");
                ResourceUtils.ExportResource("/Twil.xml", "Credentials");
            }

            Document twil = builder.build(TwilFile);
            settings.createSection("twil");
            settings.add("enabled", (twil.getRootElement().getAttributeValue("enabled")
                    .equalsIgnoreCase("true")));
            settings.add("ACCOUNT_SID", twil.getRootElement().getChildText("account_sid"));
            settings.add("MSG_SER_SID", twil.getRootElement().getChildText(
                    "messaging_service_sid"));
            settings.add("AUTH_TOKEN", twil.getRootElement().getChildText("auth_token"));

            if (! configFile.exists()) {
                Iris.warn("Warning: Could not find Config file, Generating one now!");
                ResourceUtils.ExportResource("/Config.xml", "Credentials");
            }

            Document socketDoc = builder.build(configFile);
            settings.createSection("socket");
            Element ElSock = socketDoc.getRootElement().getChild("settings")
                    .getChild("socket");
            settings.add("enabled", (ElSock.getAttributeValue("enabled")
                    .equalsIgnoreCase("true")));
            settings.add("port", Integer.parseInt(ElSock.getChildText("port")));
            settings.add("timeout", Integer.parseInt(ElSock.getChildText("timeout")));
            settings.add("concurrent", Integer.parseInt(ElSock.getChildText("concurrent")));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings;
    }
}
