package ua.kiev.prog;

/**
 * Created by Diesel on 17.02.2019.
 */
public class UserAct {

    private String name;
    private boolean act;

    public UserAct(String name, User user) {
        this.name = name;
        act = user.isAction();
    }

    @Override
    public String toString() {
        String actS = act ? "В чате" : "Отключен";
        return "@" + name + " - " + actS;
    }
}
