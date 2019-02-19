package ua.kiev.prog;

import java.util.ArrayList;
import java.util.List;

public class JsonMessages {
    private final List<Message> list;

    public JsonMessages(List<Message> sourceList, String name, int fromIndex) {
        this.list = new ArrayList<>();
        for (int i = fromIndex; i < sourceList.size(); i++) {
            Message m = sourceList.get(i);
            if (m.getTo() == null
                    || m.getFrom().equals(name)
                    || isNameTo(name, m)
                    || isNameToIntoRoom(name, m)) {
                list.add(m);
            } else {
                list.add(null);
            }
        }
    }

    private boolean isNameTo(String name, Message m) {
        String to = m.getTo();
        if (to == null) {
            return false;
        }

        return (to.charAt(0) == '@') && (to.substring(1).equals(name));
    }

    private boolean isNameToIntoRoom(String name, Message m) {
        String to = m.getTo();
        if (to == null) {
            return false;
        }

        MessageList ms = MessageList.getInstance();
        return (to.charAt(0) == '&') && (ms.isNameIntoRoom(name, to.substring(1)));
    }
}
