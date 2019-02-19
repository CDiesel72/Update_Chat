package com.company;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Diesel on 17.02.2019.
 */
public class User {

    private static String login;

    public static String getLogin() {
        return login;
    }

    public static int create() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите имя пользователя:");
        login = sc.nextLine();
        if (login.isEmpty()) {
            System.out.println("#ERROR400! Не задано имя пользователя");
            return 400;
        }
        System.out.println("Введите пароль:");
        String pass0 = sc.nextLine();
        if (pass0.isEmpty()) {
            System.out.println("#ERROR400! Не задан пароль");
            return 400;
        }
        System.out.println("Повторите пароль:");
        String pass1 = sc.nextLine();
        if (!pass0.equals(pass1)) {
            System.out.println("#ERROR400! Не корректный ввод пароля");
            return 400;
        }

        Message m = new Message(login, null, "#createuser:" + login + "=" + pass0);
        int res = SendMessage.send(Utils.getURL() + "/add", m);

        return res;
    }

    public static int checkIn() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите имя пользователя:");
        login = sc.nextLine();
        if (login.isEmpty()) {
            System.out.println("#ERROR400! Не задано имя пользователя");
            return 400;
        }
        System.out.println("Введите пароль:");
        String pass = sc.nextLine();

        Message m = new Message(login, null, "#checkinuser:" + login + "=" + pass);
        int res = SendMessage.send(Utils.getURL() + "/add", m);

        return res;
    }
}
