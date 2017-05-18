package edu.hartnell.iris.commands.commands;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.Command;

import java.util.List;

public class Clear extends Command {
    @Override
    public void onCommand(String Command, List<String> Args) {
        Iris.getConsole().clearDocument();
        Iris.respond("Document has been cleared!");
    }
}
