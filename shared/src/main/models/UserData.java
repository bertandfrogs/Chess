package models;

import com.google.gson.Gson;

import java.util.Objects;

/**
 * The UserData model is how users' data is stored in the server. AuthTokens are not stored in the UserData model because they are non-permanent (they get created and deleted every session)
 */
public class UserData {
    /**
     * The username, used by the user to log in. Connected with authTokens, and used to keep track of which player the user is.
     */
    String username;
    /**
     * The user's password, they use it to log in.
     */
    String password;
    /**
     * The user's email. Only using when registering a new user.
     */
    String email = "";

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        if (username == null ^ userData.username == null) return false;
        if (password == null ^ userData.password == null) return false;
        if (email == null ^ userData.email == null) return false;

        return (username != null && username.equals(userData.username))
                && (password != null && password.equals(userData.password))
                && (email != null && email.equals(userData.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
