package ua.kiev.prog;

import java.util.Date;

/**
 * Created by Diesel on 17.02.2019.
 */
public class User {
    private String pass;
    private Date lastAct;

    public User(String pass, Date lastAct) {
        this.pass = pass;
        this.lastAct = lastAct;
    }

    public Date getLastAct() {
        return lastAct;
    }

    public void setLastAct(Date lastAct) {
        this.lastAct = lastAct;
    }

    public String getPass() {
        return pass;
    }

    public boolean isAction() {
        Date now = new Date();
        return (now.getTime() - lastAct.getTime()) < 3000;
    }
}