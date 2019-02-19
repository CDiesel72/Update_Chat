package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by Diesel on 17.02.2019.
 */
public class SendMessage {

    public static int send(String url, Message m) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        try {
            String json = m.toJSON();
            os.write(json.getBytes(StandardCharsets.UTF_8));

        } finally {
            os.close();
        }

        InputStream is = conn.getInputStream();
        try {
            byte[] buf = Utils.requestBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            int res = conn.getResponseCode();

            Gson gson = new GsonBuilder().create();
            JsonAnswer list = gson.fromJson(strBuf, JsonAnswer.class);
            if (list != null) {
                for (Message mIn : list.getList()) {
                    if (mIn != null) {
                        System.out.println(mIn);
                    }

                    if (res == 200
                            && mIn != null
                            && mIn.getFrom().equals("Server")
                            && mIn.getText().indexOf("#ERROR400!") >= 0) {
                        res = 400;
                    }
                }
            }

            return res;
        } finally {
            is.close();
        }
    }
}
