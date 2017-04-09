package edu.hartnell.iris.data;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Contact {

    private final String NAME, NICK, POSITION, EMAIL, PHONE;

    public Contact(@NotNull String name, @Nullable String nick, @Nullable String position,
                   @NotNull String email, @Nullable String phone){
        this.NAME = name; this.NICK = nick; this.POSITION = position;
        this.EMAIL = email; this.PHONE = phone;

        if (position == null){
            position = "member";
        }
    }

    public String getName(){
        return NAME;
    }

    public String getNick(){
        if (NICK == null || NICK.equals(""))
            return NAME.split(" ")[0].toLowerCase();
        return NICK;
    }

    public String getPosition(){
        return POSITION;
    }

    public String getEmail(){
        return EMAIL;
    }

    public void saveToDB(Connection connection, String table){
        try {
            PreparedStatement statement = connection.prepareStatement("" +
                    "INSERT INTO Iris.? VALUES (?, ?, ?, ?, ?)"
            );
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
