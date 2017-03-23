package edu.hartnell.iris.gui;

import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.commands.listeners.CommandKeys;
import edu.hartnell.iris.time.TimeManager;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

import static java.lang.Thread.sleep;

public class Console extends JFrame {
    private JPanel MainPanel;
    private JTextPane InfoPanel;
    private JTextField CommandPanel;
    private JScrollPane Scroller;
    private JLabel Memory;

    public Console() {
        super("IrisFrame Console");
        setName("IrisFrame");
        setContentPane(MainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        setSize(800, 500);

        say("Console Initialized Sucessfully!");
        MemoryWatcher();
    }

    private void MemoryWatcher() {
        new Thread(() -> {
            while (true) {
                try {
                    sleep(1000);
                    Runtime rt = Runtime.getRuntime();
                    Memory.setText("Memory Usage: " + String.valueOf((
                            (rt.totalMemory() - rt.freeMemory()) / 1024) / 1024)
                            + "MB");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void initializeEvents(Console console, CommandManager commandManager){
        say("Console Events Initialized!");
        CommandPanel.addKeyListener(new CommandKeys(console, commandManager));
    }

    public void say(String Message) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                Color.GREEN, null, true);
        appendText(Message + "\n", Color.BLACK, null, false);
    }

    public void warn(String Warning) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                Color.YELLOW, null, true);
        appendText(Warning + "\n", Color.BLACK, null, true);
    }

    public void report(String Report) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                Color.RED, null, true);
        appendText(Report + "\n", Color.BLACK, null, true);
    }

    private void appendText(String Text, Color Fore, Color Back, boolean Bold) {
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        if (Fore != null)
            StyleConstants.setForeground(keyWord, Fore);
        if (Back != null)
            StyleConstants.setBackground(keyWord, Back);
        StyleConstants.setBold(keyWord, Bold);
        try {
            InfoPanel.getStyledDocument().insertString(
                    InfoPanel.getStyledDocument().getLength(), Text, keyWord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JTextField getCommandPanel() {
        return CommandPanel;
    }
}
