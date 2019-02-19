package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Diesel on 18.02.2019.
 */
public class JsonAnswer {
    private final List<Message> list;

    public JsonAnswer() {
        list = new ArrayList<>();
    }

    public List<Message> getList() {
        return Collections.unmodifiableList(list);
    }
}
