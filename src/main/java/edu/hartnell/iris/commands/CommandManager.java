package edu.hartnell.iris.commands;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.commands.Hello;
import edu.hartnell.iris.commands.commands.Memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager {

    private HashMap<String, Command> Commands;
    private List<String> PrevCommands = new ArrayList<>();

    public CommandManager() {
        Commands = new HashMap<>();
        registerCommands();
    }

    private void registerCommands() {
        Commands.put("hello", new Hello());
        Commands.put("memory", new Memory());
    }

    public void registerCommand(String commandName, Command command){
        Iris.say("Registering the command: " + commandName);
        Commands.put(commandName.toLowerCase(), command);
    }

    public void submitInterpretation(String CMD) {
        Iris.report("Submitted Command: " + CMD);
        if (!Commands.containsKey(CMD.split(" ")[0].toLowerCase())) {
            Iris.report("ERROR: \"" + CMD.split(" ")[0] +
                    "\" isn't a valid command!");
            return;
        }
        List<String> Args = new ArrayList<>();
        boolean f = true;
        for (String S : CMD.split(" ")) {
            if (f) {
                f = false;
                continue;
            }
            Args.add(S);
        }
        Commands.get(CMD.split(" ")[0].toLowerCase()).onCommand(CMD.split(" ")[0]
                .toLowerCase(), Args);

    }

    private String getPiece(String cmd, int piece){
        if (!cmd.contains(" ")) return cmd;
        return cmd.split(" ")[piece];
    }
}