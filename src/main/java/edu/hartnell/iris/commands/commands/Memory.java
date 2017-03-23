package edu.hartnell.iris.commands.commands;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.Command;

import java.util.List;

public class Memory extends Command {
    @Override
    public void onCommand(String Command, List<String> Args) {
        Runtime runtime = Runtime.getRuntime();
        long UsedMem = runtime.totalMemory() - runtime.freeMemory();
        UsedMem =  UsedMem/1024;
        Iris.say("Memory Used: " + String.valueOf(UsedMem));
    }
}
