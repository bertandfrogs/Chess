package api.models;

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
    String email;
}
