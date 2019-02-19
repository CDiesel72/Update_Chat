package ua.kiev.prog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddServlet extends HttpServlet {

    private MessageList msgList = MessageList.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);

        Message msg = Message.fromJSON(bufStr);
        String jsonOut = null;
        if (msg != null) {
            jsonOut = procMess(msg);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json");

        if (jsonOut != null) {
            OutputStream os = resp.getOutputStream();
            byte[] bufOut = jsonOut.getBytes(StandardCharsets.UTF_8);
            os.write(bufOut);

            //PrintWriter pw = resp.getWriter();
            //pw.print(json);
        }
    }

    private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    private String procMess(Message m) {
        String jOut = null;

        if (m.getText().charAt(0) == '#') {
            jOut = procCommand(m);
        } else if (m.getText().charAt(0) == '@') {
            jOut = messForUser(m);
        } else if (m.getText().charAt(0) == '&') {
            jOut = messForRoom(m);
        } else {
            jOut = msgList.messAdd(m);
        }
        return jOut;
    }

    private String procCommand(Message m) {
        String jOut = null;
        String[] comm = m.getText().substring(1).split("[:]", 2);
        if (comm.length != 2) {
            jOut = msgList.answerToJSON(new Message(Utils.getNameServer(), "@" + m.getFrom(),
                    Utils.getNameError() + " Не корректная команда"));
            return jOut;
        }

        if ("createuser".equalsIgnoreCase(comm[0])) {
            jOut = msgList.userAdd(m.getFrom(), comm[1]);
            return jOut;
        } else if ("checkinuser".equalsIgnoreCase(comm[0])) {
            jOut = msgList.userCheckIn(m.getFrom(), comm[1]);
            return jOut;
        } else if ("list".equalsIgnoreCase(comm[0])) {
            jOut = msgList.getList(m.getFrom(), comm[1]);
            return jOut;
        } else if ("createroom".equalsIgnoreCase(comm[0])) {
            jOut = msgList.roomAdd(m.getFrom(), comm[1]);
            return jOut;
        } else if ("deleteroom".equalsIgnoreCase(comm[0])) {
            jOut = msgList.roomDel(m.getFrom(), comm[1]);
            return jOut;
        } else if ("addtoroom".equalsIgnoreCase(comm[0])) {
            jOut = msgList.roomUserAdd(m.getFrom(), comm[1]);
            return jOut;
        } else if ("delfromroom".equalsIgnoreCase(comm[0])) {
            jOut = msgList.roomUserDel(m.getFrom(), comm[1]);
            return jOut;
        }

        jOut = msgList.answerToJSON(new Message(Utils.getNameServer(), "@" + m.getFrom(),
                Utils.getNameError() + " Не известная команда " + comm[0]));
        return jOut;
    }

    private String messForUser(Message m) {
        String[] comm = m.getText().substring(1).split("[:]", 2);
        if (comm.length != 2) {
            return msgList.answerToJSON(new Message(Utils.getNameServer(), "@" + m.getFrom(),
                    Utils.getNameError() + " Не корректная команда"));
        }
        String jOut = msgList.messForUserAdd(m.getFrom(), comm[0], comm[1]);
        return jOut;
    }

    private String messForRoom(Message m) {
        String[] comm = m.getText().substring(1).split("[:]", 2);
        if (comm.length != 2) {
            return msgList.answerToJSON(new Message(Utils.getNameServer(), "@" + m.getFrom(),
                    Utils.getNameError() + " Не корректная команда"));
        }
        String jOut = msgList.messForRoomAdd(m.getFrom(), comm[0], comm[1]);
        return jOut;
    }
}
