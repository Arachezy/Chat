package server;

import main.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Никита on 08.03.2015.
 */
public class Server {

    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket server;

    private class Connection extends Thread {
        private BufferedReader inData;
        private PrintWriter outData;
        private Socket socket;
        private String name = "";

        public Connection(Socket socket) {
            this.socket = socket;

            try {
                inData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outData = new PrintWriter(socket.getOutputStream(), true);

            } catch (IOException e) {
                e.printStackTrace();
                close();
            }

        }

        public void close() {
            try {
                inData.close();
                outData.close();
                socket.close();
                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }}

            public void run() {
                try {
                    name = inData.readLine();
                    // Отправляем всем клиентам сообщение о том, что зашёл новый пользователь
                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext()) {
                            ((Connection) iter.next()).outData.println(name + " cames now");
                        }
                    }

                    String str = "";
                    while (true) {
                        str = inData.readLine();
                        if(str.equals("exit")) break;

                        // Отправляем всем клиентам очередное сообщение
                        synchronized(connections) {
                            Iterator<Connection> iter = connections.iterator();
                            while(iter.hasNext()) {
                                ((Connection) iter.next()).outData.println(name + ": " + str);
                            }
                        }
                    }

                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext()) {
                            ((Connection) iter.next()).outData.println(name + " has left");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close();
                }
            }
    }

    public Server() {
        try {
            server = new ServerSocket(Const.port);
            while (true) {
                Socket socket = server.accept();
                Connection con = new Connection(socket);
                connections.add(con);
                con.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();
            synchronized(connections) {
                Iterator<Connection> iter = connections.iterator();
                while(iter.hasNext()) {
                    ((Connection) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    }
