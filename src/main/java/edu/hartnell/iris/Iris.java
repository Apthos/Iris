package edu.hartnell.iris;

import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.data.DataManager;
import edu.hartnell.iris.email.EmailManager;
import edu.hartnell.iris.gui.Console;
import edu.hartnell.iris.plugin.PluginManager;
import edu.hartnell.iris.utility.ResourceUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;

public class Iris {

    private static Console console;
    private static CommandManager commandManager;
    private static PluginManager pluginManager;
    private static EmailManager emailManager;

    public static void main(String... args){
        System.out.println("Initializing server!");
        setup();
    }

    private static void setup() {
        console = new Console();
        HashMap<String, HashMap<String, String>> credentials = collectCredentials();
        commandManager = new CommandManager();
        console.initializeEvents(console, commandManager);
        emailManager = new EmailManager(credentials.get("EMail").get("USER"),
                credentials.get("EMail").get("PASS"));
        DataManager dataManager = new DataManager(credentials.get("MySQL").get("HOST"),
                credentials.get("MySQL").get("PORT"),
                credentials.get("MySQL").get("USER"),
                credentials.get("MySQL").get("PASS"));
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

    public static PluginManager getPluginManager(){
        return pluginManager;
    }

    public static EmailManager getEmailManager(){
        return emailManager;
    }

    public static File getRuntimeLocation() throws URISyntaxException{
        File file = new File(Iris.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI().getPath());
        return file.getParentFile();
    }

    public static File getRuntimeJar() throws URISyntaxException {
        File file = new File(Iris.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI().getPath());
        return file;
    }

    private static HashMap<String, HashMap<String, String>> collectCredentials(){
        HashMap<String, HashMap<String, String>> credentials = new HashMap<>();
        try {
            File cDir = new File(Iris.getRuntimeLocation() + "/Credentials");
            File EMailFile = new File(cDir + "/EMail.xml");
            File MySQLFile = new File(cDir + "/MySQL.xml");

            SAXBuilder builder = new SAXBuilder();

            if (!cDir.exists()) {
                Iris.warn("Warning: Could not find credentials folder, Generating new one!");
                cDir.mkdir();
                Iris.warn("Copying login.xml to credentials folder!");
            }

            if (!EMailFile.exists()){
                Iris.warn("Warning: Could not find EMail file, Generating new one!");
                Iris.warn("Copying EMail.xml to credentials folder!");
                ResourceUtils.ExportResource("/EMail.xml", "Credentials");
            } else {
                Document EMail = builder.build(EMailFile);
                HashMap<String, String> data = new HashMap<>();
                data.put("USER", EMail.getRootElement().getChild("email")
                        .getChildText("address"));
                data.put("PASS", EMail.getRootElement().getChild("email")
                        .getChildText("password"));
                data.put("ENABLED", (EMail.getRootElement().getAttributeValue("enabled")
                        .equalsIgnoreCase("true")) ? "true" : "false");
                credentials.put("EMail", data);
            }

            if (!MySQLFile.exists()){
                Iris.warn("Warning: Could not find MySQL file, Generating new one!");
                Iris.warn("Copying MySQL.xml to credentials folder!");
                ResourceUtils.ExportResource("/MySQL.xml", "Credentials");
            } else {
                Document mysql = builder.build(MySQLFile);
                HashMap<String, String> data = new HashMap<>();
                data.put("HOST", mysql.getRootElement().getChildText("host"));
                data.put("PORT", mysql.getRootElement().getChildText("port"));
                data.put("USER", mysql.getRootElement().getChildText("user"));
                data.put("PASS", mysql.getRootElement().getChildText("pass"));
                data.put("ENABLED", (mysql.getRootElement().getAttributeValue("enabled")
                        .equalsIgnoreCase("true")) ? "true" : "false");
                credentials.put("MySQL", data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return credentials;
    }

}
