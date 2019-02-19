package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMessages {
    private final List<Message> list;

    public JsonMessages() {
        list = new ArrayList<>();
    }

    public List<Message> getList() {
        return Collections.unmodifiableList(list);
    }
}
