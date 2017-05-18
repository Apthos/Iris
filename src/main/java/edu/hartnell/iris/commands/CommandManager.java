package edu.hartnell.iris.commands;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.commands.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager {

    private Table<String, Command, String> Commands;
    private List<String> PrevCommands = new ArrayList<>();

    public CommandManager() {
        Commands = HashBasedTable.create();
        registerCommands();
    }

    private void registerCommands() {
        registerCommand("hello", new Hello(), "Just a test command");
        registerCommand("memory", new Memory(), "Check Memory Usage");
        registerCommand("reload", new Reload(), "Reloads Plugins");
        registerCommand("help", new Help(), "Shows all commands and description");
        registerCommand("clear", new Clear(), "Clears Document");
    }

    public void clear() {
        Commands.clear();
        registerCommands();
    }

    public Table<String, Command, String> getCommands() {
        return this.Commands;
    }

    public void registerCommand(String commandName, Command command, String desc) {
        Commands.put(commandName.toLowerCase(), command, desc);
    }

    public void submitInterpretation(String CMD) {
        if (Commands.row(CMD.split(" ")[0].toLowerCase()).isEmpty()) {
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
        ((Command) Commands.row(CMD.split(" ")[0].toLowerCase()).keySet().toArray()[0])
                .onCommand(CMD.split(" ")[0].toLowerCase(), Args);
    }

    private String getPiece(String cmd, int piece){
        if (!cmd.contains(" ")) return cmd;
        return cmd.split(" ")[piece];
    }
}