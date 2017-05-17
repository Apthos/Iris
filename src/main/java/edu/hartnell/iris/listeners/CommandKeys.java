package edu.hartnell.iris.listeners;

import edu.hartnell.iris.Iris;
import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.gui.Console;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class CommandKeys extends KeyAdapter {

    private Console console;
    private CommandManager commandManager;

    private LinkedList<String> prevCommands = new LinkedList<>();

    public CommandKeys(Console console, CommandManager commandManager){
        this.console = console;
        this.commandManager = commandManager;
    }

    private void submitCommand(String cmd) {
        console.getCommandPanel().setText("");
        commandManager.submitInterpretation(cmd);
        prevCommands.add(cmd);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            if (console.getCommandPanel().getText().equals(""))
                return;

            submitCommand(Iris.getConsole().getCommandPanel().getText());

        } else if (e.getKeyCode() == KeyEvent.VK_UP) {

            if (!console.getCommandPanel().getText().equals(""))
                return;
            console.getCommandPanel().setText(prevCommands.getLast());

        }
    }
}
