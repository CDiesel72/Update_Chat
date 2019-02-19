package com.company;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            boolean first = true;
            String menu = "";
            do {
                if (!first) {
                    System.out.println("Не корректный ввод, попробуйте еще раз");
                    System.out.println("--------");
                }
                first = false;
                System.out.println("Выберите действие:");
                System.out.println("Войти под своим логином - 1");
                System.out.println("Зарегистрироваться - 2");
                System.out.println("Выход - 0");
                menu = scanner.nextLine();
                System.out.println("--------");
            } while (!("1".equals(menu) || "2".equals(menu) || "0".equals(menu) || "".equals(menu)));


            int res = 200;
            if ("1".equals(menu)) {
                res = User.checkIn();
            } else if ("2".equals(menu)) {
                res = User.create();
            } else {
                return;
            }

            if (res != 200) { // 200 OK
                System.out.println("HTTP error occured: " + res);
                return;
            }

            Thread th = new Thread(new GetThread());
            th.setDaemon(true);
            th.start();

            help();

            System.out.println("Вводите сообщения и команды: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty()) break;

                if ("#".equals(text)) {
                    help();
                    continue;
                }


                Message m = new Message(User.getLogin(), null, text);
                res = SendMessage.send(Utils.getURL() + "/add", m);

                if (res != 200) { // 200 OK
                    System.out.println("HTTP error occured: " + res);
                    return;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void help() {
        System.out.println("Доступные команды:");
        System.out.println("# - показать этот список");
        System.out.println("#List:users - просмотреть список зарегистрированных пользователей и их статусы");
        System.out.println("#List:rooms - просмотреть список зарегистрированных комнат");
        System.out.println("#List:&имя_комнаты - просмотреть список зарегистрированных пользователей в комнате");
        System.out.println("#CreateRoom:имя_комнаты - создать комнату");
        System.out.println("#DeleteRoom:имя_комнаты - удалить комнату");
        System.out.println("#AddToRoom:имя_комнаты - добавить себя в комнату");
        System.out.println("#DelFromRoom:имя_комнаты - удалить себя из комнаты");
        System.out.println("Формат сообщений:");
        System.out.println("текст_сообщения - сообщение всем зарегистрированным пользователям");
        System.out.println("@имя_пользователя:текст_сообщения - приватное сообщение");
        System.out.println("&имя_комнаты:текст_сообщения - сообщение для пользователей зарегистрированных в комнате");
        System.out.println("--------");
    }
}
