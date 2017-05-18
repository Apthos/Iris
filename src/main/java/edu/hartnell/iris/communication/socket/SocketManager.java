package edu.hartnell.iris.communication.socket;

import edu.hartnell.iris.Iris;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketManager {

    private ServerSocket serverSocket = null;
    private final int port;
    private final int timeout;
    private final int concurrent;
    private static ConcurrentLinkedQueue<SockThread> sockThreads =
            new ConcurrentLinkedQueue<>();
    private boolean acceptingConnections = true;
    private boolean socketMonitor = true;


    public SocketManager(int port, int timeout, int concurrent) {
        this.port = port;
        this.timeout = timeout;
        this.concurrent = concurrent;
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) { Iris.report("Could not bind to port!"); return; }
        new Thread(() -> {
            while (acceptingConnections) {
                try {
                    Socket socket = serverSocket.accept();
                    if (sockThreads.size() >= concurrent) {
                        socket.close();
                        continue;
                    }
                    SockThread sock = new SockThread(socket);
                    Thread.sleep(100);
                } catch (Exception e) { }
            }
        }, "Socket Router").start();

        new Thread(() -> {
            while (socketMonitor) {
                for (SockThread sockThread : sockThreads) {
                    Iris.report(String.valueOf(sockThread.ID) + " is " +
                            String.valueOf(getDifferenceBySeconds(
                                    sockThread.getLastCommunicationTime(), LocalTime.now()
                            )) + " old!");
                    if (getDifferenceBySeconds(sockThread.getLastCommunicationTime(),
                            LocalTime.now()) > 5) {
                        Iris.report("Killing the thread " + sockThread.ID + "!");
                        sockThread.kill();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) { }
            }
        }, "Socket Manager").start();

        Iris.say("Succesfully binded to port " + port + ".");
    }

    private class SockThread implements Runnable {

        private Socket socket = null;
        private LocalTime lastCom;
        private boolean dead = false;
        public final int ID = (int) Math.random() * 1000;
        private Thread thread;

        public SockThread(Socket socket) {
            Iris.say("Socket Thread Created!");
            this.socket = socket;
            lastCom = LocalTime.now();
            sockThreads.add(this);
            thread = Thread.currentThread();
            Iris.warn("Starting thread runnable!" + "  Current Mem Usage: " +
                    Iris.getConsole().getMemory());
            run();
        }

        private boolean authenticated = false;

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                String line = "";
                Iris.warn("before inner while loop!" + "  Current Mem Usage: " +
                        Iris.getConsole().getMemory());
                while (! dead) {
                    line = br.readLine();
                    if (line == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    if (line.equalsIgnoreCase("end")) {
                        bw.write("CONNECTION TERMINATED");
                        bw.flush();
                        break;
                    }
                    String response = "";
                    if (! authenticated) {
                        response = "Please Authenticate yourself!\n";
                        bw.write(response);
                    } else {
                        response = "PHP said: " + line + "\n";
                        bw.write(response);
                    }
                    bw.flush();
                    Iris.say("Received: " + line);
                    Iris.say("Response: " + response);
                    this.lastCom = LocalTime.now();
                }

                if (dead) {
                    Iris.report("Socket Connection timed out!");
                }

                bw.close();
                br.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void kill() {
            dead = true;
            sockThreads.remove(this);
        }


        public LocalTime getLastCommunicationTime() {
            return this.lastCom;
        }

    }

    public int getDifferenceBySeconds(LocalTime before, LocalTime after) {
        if (before.getHour() == after.getHour()) {
            int bt = (60 * before.getMinute()) + before.getSecond();
            int ba = (60 * after.getMinute()) + after.getSecond();
            return Math.abs(bt - ba);
        }
        return 3600;
    }

    public void kill() {
        try {
            serverSocket.close();
            acceptingConnections = false;
            socketMonitor = false;
            sockThreads.clear();
        } catch (IOException e) {
            Iris.report("Problem killing socket manager!");
        }
    }

}
