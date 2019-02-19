package ua.kiev.prog;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MessageList {
    private static final MessageList msgList = new MessageList();

    private final Gson gson;
    private final List<Message> list = new LinkedList<>();
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, List<String>> rooms = new HashMap<>();

    public static MessageList getInstance() {
        return msgList;
    }

    private MessageList() {
        gson = new GsonBuilder().create();
    }

    public synchronized String messAdd(Message m) {
        list.add(m);
        return answerToJSON();
    }

    public synchronized String messToJSON(String name, int n) {
        if (n >= list.size()) return null;
        return gson.toJson(new JsonMessages(list, name, n));
    }

    public String answerToJSON(List<Message> list) {
        return gson.toJson(new JsonAnswer(list));
    }

    public String answerToJSON(Message mess) {
        return gson.toJson(new JsonAnswer(mess));
    }

    public String answerToJSON() {
        return gson.toJson(new JsonAnswer());
    }

    public synchronized String userAdd(String name, String str) {
        String[] comm = str.split("[=]", 2);
        if (comm.length != 2) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError400() + " Не корректная команда"));
        }
        if (users.containsKey(comm[0])) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError400() + " Пользователь " + comm[0] + " уже существует"));
        }
        users.put(comm[0], new User(comm[1], new Date()));
        return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                "Пользователь " + comm[0] + " успешно зарегистрирован"));
    }

    public synchronized String userCheckIn(String name, String str) {
        String[] comm = str.split("[=]", 2);
        if (comm.length != 2) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError400() + " Не корректная команда"));
        }

        User user = users.get(comm[0]);
        if (user == null) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError400() + " Пользователь " + comm[0] + " не найден"));
        }
        if (!user.getPass().equals(comm[1])) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError400() + " Пароль пользователя " + comm[0] + " указан не верно"));
        }

        user.setLastAct(new Date());
        return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                "Пользователь " + comm[0] + " вошел в чат"));
    }

    public synchronized void userAct(String name) {
        User user = users.get(name);
        if (user != null) {
            user.setLastAct(new Date());
        }
    }

    public synchronized String messForUserAdd(String from, String to, String text) {
        messAdd(new Message(from, "@" + to, text));
        if (users.containsKey(to)) {
            return answerToJSON();
        } else {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + from,
                    "Пользователь " + to + " не зарегистрирован в чате."
                            + " Если такой пользователь зарегистрируется, он получит Ваше сообщение"));
        }
    }

    public synchronized String getList(String name, String str) {
        if ("users".equals(str)) {
            Set<String> usersSet = users.keySet();
            List<Message> lMess = new ArrayList<>();
            lMess.add(new Message(Utils.getNameServer(), "@" + name,
                    "Список пользователей зарегистрированных в чате:"));
            for (String nm : usersSet) {
                User user = users.get(nm);
                UserAct userAct = new UserAct(nm, user);
                lMess.add(new Message(Utils.getNameServer(), "@" + name, userAct.toString()));
            }
            return answerToJSON(lMess);
        } else if ("rooms".equals(str)) {
            Set<String> roomsSet = rooms.keySet();
            List<Message> lMess = new ArrayList<>();
            lMess.add(new Message(Utils.getNameServer(), "@" + name,
                    "Список зарегистрированных комнат:"));
            for (String room : roomsSet) {
                lMess.add(new Message(Utils.getNameServer(), "@" + name,
                        "&" + room + " - Количество пользователей: " + rooms.get(room).size()));
            }
            return answerToJSON(lMess);
        } else if (str.charAt(0) == '&') {
            String nameRoom = str.substring(1);
            if (!rooms.containsKey(nameRoom)) {
                return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                        "Комнаты " + nameRoom + " не существует"));
            }
            List<Message> lMess = new ArrayList<>();
            lMess.add(new Message(Utils.getNameServer(), "@" + name,
                    "Список пользователей зарегистрированных в комнате " + nameRoom + ":"));
            for (String nm : rooms.get(nameRoom)) {
                User user = users.get(nm);
                UserAct userAct = new UserAct(nm, user);
                lMess.add(new Message(Utils.getNameServer(), "@" + name, userAct.toString()));
            }
            return answerToJSON(lMess);
        } else {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    Utils.getNameError() + " Не корректная команда"));
        }
    }

    private synchronized String getRoomAdmin(String nameRoom) {
        return rooms.get(nameRoom).get(0);
    }

    public synchronized boolean isNameIntoRoom(String name, String nameRoom) {
        return rooms.get(nameRoom).contains(name);
    }

    public synchronized String roomAdd(String name, String nameRoom) {
        if (rooms.containsKey(nameRoom)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Комната " + nameRoom + " уже существует"));
        }

        rooms.put(nameRoom, new ArrayList<String>());
        messAdd(new Message(Utils.getNameServer(), null, "Пользователь " + name + " создал комнату " + nameRoom));

        roomUserAdd(name, nameRoom);
        return answerToJSON();
    }

    public synchronized String roomDel(String name, String nameRoom) {
        if (!rooms.containsKey(nameRoom)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете удалить комнату " + nameRoom + ". Такой комнаты не существует"));
        }

        if (!name.equals(getRoomAdmin(nameRoom))) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете удалить комнату " + nameRoom + ". Вы не администратор этой комнаты"));
        }

        rooms.remove(nameRoom);
        messAdd(new Message(Utils.getNameServer(), null,
                "Пользователь " + name + " удалил комнату " + nameRoom));
        return answerToJSON();
    }

    public synchronized String roomUserAdd(String name, String nameRoom) {
        if (!rooms.containsKey(nameRoom)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете войти в комнату " + nameRoom + ". Такой комнаты не существует"));
        }

        rooms.get(nameRoom).add(name);
        messAdd(new Message(Utils.getNameServer(), "&" + nameRoom,
                "Пользователь " + name + " вошел в комнату " + nameRoom));
        return answerToJSON();
    }

    public synchronized String roomUserDel(String name, String nameRoom) {
        if (!rooms.containsKey(nameRoom)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете выйти из комнаты " + nameRoom + ". Такой комнаты не существует"));
        }

        if (name.equals(getRoomAdmin(nameRoom))) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете выйти из комнаты " + nameRoom + ". Вы администратор этой комнаты"));
        }

        if (!rooms.get(nameRoom).contains(name)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + name,
                    "Вы не можете выйти из комнаты " + nameRoom + ". Вы не зарегистрованы в этой комнате"));
        }

        rooms.get(nameRoom).remove(name);
        messAdd(new Message(Utils.getNameServer(), "&" + nameRoom,
                "Пользователь " + name + " вышел из комнаты " + nameRoom));
        return answerToJSON();
    }

    public synchronized String messForRoomAdd(String from, String to, String text) {
        if (!rooms.containsKey(to)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + from,
                    "Вы не можете слать сообщения в комнату " + to + ". Такой комнаты не существует"));
        }

        if (!rooms.get(to).contains(from)) {
            return answerToJSON(new Message(Utils.getNameServer(), "@" + from,
                    "Вы не можете посылать сообщения в комнату " + to + ". Вы не зарегистрованы в этой комнате"));
        }

        return messAdd(new Message(from, "&" + to, text));
    }
}
