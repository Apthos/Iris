package edu.hartnell.iris.commands.commands;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.Command;

import java.util.List;

public class Hello extends Command {


    @Override
    public void onCommand(String Command, List<String> Args) {
        Iris.say("Sucessfully Played Hello Command!");
        Iris.say("Hey Kevin!");
    }

}
