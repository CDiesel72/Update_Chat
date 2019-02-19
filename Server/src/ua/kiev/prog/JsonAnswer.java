package ua.kiev.prog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diesel on 18.02.2019.
 */
public class JsonAnswer {
    private final List<Message> list;

    public JsonAnswer(List<Message> list) {
        this.list = list;
    }

    public JsonAnswer(Message mess) {
        list = new ArrayList<>();
        list.add(mess);
    }

    public JsonAnswer() {
        list = new ArrayList<>();
    }
}
