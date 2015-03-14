package client;

import main.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Никита on 08.03.2015.
 */
public class Client {
    String ip;
    private Socket socket;
    BufferedReader inData = null;
    PrintWriter outData = null;

    public Client() throws IOException {
        Scanner scan = new Scanner(System.in);

        System.out.println("Введите IP для подключения к серверу.");
        System.out.println("Формат: xxx.xxx.xxx.xxx");
        String ip = scan.nextLine();
        try {
            Socket socket = new Socket(ip, Const.port);

            try {
                inData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outData = new PrintWriter(socket.getOutputStream(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Введите свой ник:");
            outData.println(scan.nextLine());
            Resender resend = new Resender();
            resend.start();
            String text = "";
            while (!text.equals("exit")) {
                text = scan.nextLine();
                outData.println(text);
            }
            resend.setStop();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            inData.close();
            outData.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private class Resender extends Thread {
        private boolean stopped;

        public void setStop() {
            stopped = true;
        }
        @Override
        public void run() {
            try {
                while (!stopped) {
                    String str = inData.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }
}


