package edu.hartnell.iris;

import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.data.DataManager;
import edu.hartnell.iris.email.EmailManager;
import edu.hartnell.iris.test.ListenerTest;
import edu.hartnell.iris.gui.Console;
import edu.hartnell.iris.plugin.PluginManager;
import edu.hartnell.iris.text.TextManager;
import edu.hartnell.iris.utility.ResourceUtils;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Iris {

    private static Console console = null;
    private static CommandManager commandManager = null;
    private static PluginManager pluginManager = null;
    private static EmailManager emailManager = null;
    private static TextManager textManager = null;
    private static DataManager dataManager = null;

    public static void main(String... args) {
        System.out.println("Initializing server!");
        setup();
        say("Done initializing server!");

        getPluginManager().registerListener(new ListenerTest());
    }

    private static void setup() {
        console = new Console();
        HashSet<ManagerSettings> credentials = collectCredentials();
        commandManager = new CommandManager();
        console.initializeEvents(console, commandManager);
        ManagerSettings ems = ManagerSettings.getFromCollection(credentials, "email");
        if (ems.isEnabled()) {
            Iris.warn("Iris is running with emails enabled!");
            emailManager = new EmailManager(ems.get("USER"), ems.get("PASS"));
        } else {
            Iris.warn("Iris is running with emails disabled!");
        }
        ManagerSettings tms = ManagerSettings.getFromCollection(credentials, "twil");
        if (tms.isEnabled()) {
            Iris.warn("Iris is running with texts enabled!");
            textManager = new TextManager(tms.get("ACCOUNT_SID"),
                    tms.get("MSG_SER_SID"), tms.get("AUTH_TOKEN"));
        } else {
            Iris.warn("Iris is running with texts disabled!");
        }
        ManagerSettings mysqls = ManagerSettings.getFromCollection(credentials, "mysql");
        if (mysqls.isEnabled()) {
            Iris.warn("Iris is running with databases enabled!");
            dataManager = new DataManager(mysqls.get("HOST"), mysqls.get("PORT"),
                    mysqls.get("USER"), mysqls.get("PASS"));
        } else {
            Iris.warn("Iris is running with databases disabled!");
        }
        pluginManager = new PluginManager(); //has to be last
    }

    public static void say(String Message) {
        console.say(Message);
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

    private static class ManagerSettings {

        public static ManagerSettings getFromCollection(
                Collection<ManagerSettings> collection, String name) {
            for (ManagerSettings MS : collection) {
                if (MS.getName().equalsIgnoreCase(name)) {
                    return MS;
                }
            }
            return null;
        }

        private final String NAME;
        private final boolean ENABLED;
        private HashMap<String, String> settings = new HashMap<>();

        public ManagerSettings(String managerName, boolean enabled) {
            NAME = managerName;
            ENABLED = enabled;
        }

        public void add(String setting, String value) {
            settings.put(setting, value);
        }

        public String get(String setting) {
            return settings.get(setting);
        }

        public String getName() {
            return NAME;
        }

        public boolean isEnabled() {
            return ENABLED;
        }


    }

    private static HashSet<ManagerSettings> collectCredentials() {
        HashSet<ManagerSettings> credentials = new HashSet<>();
        try {
            File cDir = new File(Iris.getRuntimeLocation() + "/Credentials");
            File EMailFile = new File(cDir + "/EMail.xml");
            File MySQLFile = new File(cDir + "/MySQL.xml");
            File TwilFile = new File(cDir + "/Twil.xml");

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
            ManagerSettings ems = new ManagerSettings("email", Boolean.parseBoolean(
                    EMail.getRootElement().getAttributeValue("enabled")));
            ems.add("USER", EMail.getRootElement().getChild("email")
                    .getChildText("address"));
            ems.add("PASS", EMail.getRootElement().getChild("email")
                    .getChildText("password"));
            ems.add("ENABLED", (EMail.getRootElement().getAttributeValue("enabled")
                    .equalsIgnoreCase("true")) ? "true" : "false");
            credentials.add(ems);


            if (! MySQLFile.exists()) {
                Iris.warn("Warning: Could not find MySQL file, Generating new one!");
                ResourceUtils.ExportResource("/MySQL.xml", "Credentials");
            }
            Document mysql = builder.build(MySQLFile);
            ManagerSettings mysqls = new ManagerSettings("mysql", Boolean.parseBoolean(
                    mysql.getRootElement().getAttributeValue("enabled")));
            mysqls.add("HOST", mysql.getRootElement().getChildText("host"));
            mysqls.add("PORT", mysql.getRootElement().getChildText("port"));
            mysqls.add("USER", mysql.getRootElement().getChildText("user"));
            mysqls.add("PASS", mysql.getRootElement().getChildText("pass"));
            credentials.add(mysqls);

            if (! TwilFile.exists()) {
                Iris.warn("Warning: Could not find Twilio login file, Generating new one!");
                ResourceUtils.ExportResource("/Twil.xml", "Credentials");
            }
            Document twil = builder.build(TwilFile);
            ManagerSettings twils = new ManagerSettings("twil", Boolean.parseBoolean(
                    twil.getRootElement().getAttributeValue("enabled")));
            twils.add("ACCOUNT_SID", twil.getRootElement().getChildText("account_sid"));
            twils.add("MSG_SER_SID", twil.getRootElement().getChildText(
                    "messaging_service_sid"));
            twils.add("AUTH_TOKEN", twil.getRootElement().getChildText("auth_token"));
            credentials.add(twils);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return credentials;
    }

}
