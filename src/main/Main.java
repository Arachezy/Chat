package main;

import client.Client;
import server.Server;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Никита on 08.03.2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
            Scanner in = new Scanner(System.in);

            System.out.println("Запустить программу в режиме сервера или клиента? (S(erver) / C(lient))");
            while (true) {
                char answer = Character.toLowerCase(in.nextLine().charAt(0));
                if (answer == 's') {
                    new Server();
                    break;
                } else {
                    if (answer == 'c') {
                        new Client();
                        break;
                    } else {
                        System.out.println("Некорректный ввод. Повторите.");
                    }
                }
            }
        }
    }

