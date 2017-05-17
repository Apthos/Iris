package edu.hartnell.iris.gui;

import edu.hartnell.iris.commands.CommandManager;
import edu.hartnell.iris.listeners.CommandKeys;
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

    private final Color Base;
    private final Color NGreen;
    private final Color LGreen;
    private final Color NBlue;
    private final Color DBlue;
    private final Color DOrange;
    private final Color NRed;
    private final Color NPink;
    private final Color NYellow;
    private final Color Purple;

    {
        float[] baseCode = Color.RGBtoHSB(192, 255, 0, null);
        NGreen = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        NGreen.brighter();
        baseCode = Color.RGBtoHSB(171, 227, 100, null);
        LGreen = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(167, 219, 216, null);
        NBlue = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(240, 119, 53, null);
        DOrange = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(116, 104, 100, null);
        NRed = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(222, 83, 112, null);
        NPink = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(253, 253, 69, null);
        NYellow = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(200, 146, 234, null);
        Purple = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        baseCode = Color.RGBtoHSB(0, 173, 217, null);
        DBlue = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
    }

    public Console() {
        setName("Iris");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //if (false) {

        float[] baseCode = Color.RGBtoHSB(33, 33, 33, null);
        Base = Color.getHSBColor(baseCode[0], baseCode[1], baseCode[2]);
        float[] outCode = Color.RGBtoHSB(38, 50, 56, null);
        Color out = Color.getHSBColor(outCode[0], outCode[1], outCode[2]);

        Scroller.setBackground(Base);
        Scroller.setBorder(BorderFactory.createMatteBorder(7, 7, 0, 7, out));
        Scroller.getVerticalScrollBar().setBackground(Base);
        Scroller.getVerticalScrollBar().setForeground(Base);


        InfoPanel.setBackground(Base);
        InfoPanel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));


        MainPanel.setBackground(out);
        CommandPanel.setMargin(new Insets(10, 10, 10, 10));
        CommandPanel.setBackground(Base);
        CommandPanel.setCaretColor(Color.white);
        CommandPanel.setBorder(BorderFactory.createMatteBorder(0, 7, 7, 7, out));
        CommandPanel.setForeground(Color.WHITE);
        CommandPanel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        //}

        setContentPane(MainPanel);
        pack();
        setVisible(true);

        setSize(800, 500);

        say("Console Initialized Sucessfully!");
        MemoryWatcher();
    }

    private int memory = 0;

    private void MemoryWatcher() {
        new Thread(() -> {
            while (true) {
                try {
                    sleep(100);
                    Runtime rt = Runtime.getRuntime();
                    memory = (int) ((rt.totalMemory() - rt.freeMemory()) / 1024) / 1024;
                    Memory.setText("Memory Usage: " + String.valueOf(memory) + "MB");
                    if (memory < 30) {
                        Memory.setForeground(NGreen);
                    } else if (memory < 100) {
                        Memory.setForeground(NYellow);
                    } else {
                        Memory.setForeground(NRed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Memory Watcher").start();
    }

    public int getMemory() {
        return memory;
    }

    public void initializeEvents(Console console, CommandManager commandManager){
        say("Console Events Initialized!");
        CommandPanel.addKeyListener(new CommandKeys(console, commandManager));
    }

    public void say(String Message) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                NGreen, null, true, true);
        appendText(Message, NBlue, null, false, false);
    }

    public void respond(String Message) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                Purple, null, true, true);
        appendText(Message, DBlue, null, false, false);
    }

    public void warn(String Warning) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                DOrange, null, true, true);
        appendText(Warning, LGreen, null, true, false);
    }

    public void report(String Report) {
        appendText("[" + TimeManager.getStringTimeFormatted() + "] ",
                Color.RED, null, true, true);
        appendText(Report, NPink, null, true, false);
    }

    private boolean first = true;

    private void appendText(String Text, Color Fore, Color Back, boolean Bold,
                            boolean timestamp) {
        if (timestamp)
            if (first)
                first = false;
            else
                Text = "\n" + Text;
        Scroller.getVerticalScrollBar().setValue(
                Scroller.getVerticalScrollBar().getMaximum());
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
