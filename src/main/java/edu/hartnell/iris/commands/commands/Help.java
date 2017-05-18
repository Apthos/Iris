package edu.hartnell.iris.commands.commands;

import com.google.common.collect.Table;
import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.Command;

import java.util.List;

public class Help extends Command {
    @Override
    public void onCommand(String Command, List<String> Args) {
        Iris.respond("Commands: ");
        Table<String, Command, String> commands = Iris.getCommandManager().getCommands();
        commands.rowKeySet().forEach(((key) ->
                Iris.respond(key + " - " + commands.row(key).values().iterator().next())));
    }
}
