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
    private String currentCommand = "";

    public CommandKeys(Console console, CommandManager commandManager){
        Iris.report("Program Created new instance of keylogger now!");
        this.console = console;
        this.commandManager = commandManager;
    }

    private void submitCommand(){
        console.getCommandPanel().setText("");
        Iris.report("Submitting command: " + currentCommand);
        commandManager.submitInterpretation(currentCommand);
        prevCommands.add(currentCommand);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (console.getCommandPanel().getText().equals(""))
                return;
            submitCommand();
            currentCommand = "";
            Iris.say("Current Command should be cleared!");
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (!console.getCommandPanel().getText().equals(""))
                return;
            console.getCommandPanel().setText(prevCommands.getLast());
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (currentCommand.equals(""))
                return;
            if (currentCommand.length() == 1){
                currentCommand = "";
                return;
            }
            currentCommand = currentCommand.substring(0, currentCommand.length()-1);
        } else if (Character.isAlphabetic(e.getKeyChar()) ||
                Character.isJavaLetterOrDigit(e.getKeyChar()) ||
                e.getKeyCode() == KeyEvent.VK_SPACE) {
            currentCommand = currentCommand + e.getKeyChar();
            //Iris.say("Appending command: " + currentCommand);
        }
    }
}
